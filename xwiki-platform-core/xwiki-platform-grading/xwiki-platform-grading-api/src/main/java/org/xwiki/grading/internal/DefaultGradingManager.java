/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.grading.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.observation.ObservationManager;
import org.xwiki.observation.event.Event;
import org.xwiki.grading.AverageGrading;
import org.xwiki.grading.Grading;
import org.xwiki.grading.GradingConfiguration;
import org.xwiki.grading.GradingException;
import org.xwiki.grading.GradingManager;
import org.xwiki.grading.events.CreatedGradingEvent;
import org.xwiki.grading.events.DeletedGradingEvent;
import org.xwiki.grading.events.UpdatedAverageGradingEvent;
import org.xwiki.grading.events.UpdatedGradingEvent;
import org.xwiki.grading.internal.AverageGradingSolrCoreInitializer.AverageGradingQueryField;
import org.xwiki.search.solr.Solr;
import org.xwiki.search.solr.SolrException;
import org.xwiki.search.solr.SolrUtils;
import org.xwiki.user.UserReference;
import org.xwiki.user.UserReferenceResolver;
import org.xwiki.user.UserReferenceSerializer;

/**
 * Default implementation of {@link GradingManager} which stores Grading and AverageGrading in Solr.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@Component
@Named("solr")
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class DefaultGradingManager implements GradingManager
{
    @Inject
    private SolrUtils solrUtils;

    @Inject
    private Solr solr;

    @Inject
    private UserReferenceSerializer<String> userReferenceSerializer;

    @Inject
    private EntityReferenceSerializer<String> entityReferenceSerializer;

    @Inject
    private UserReferenceResolver<String> userReferenceResolver;

    @Inject
    private EntityReferenceResolver<String> entityReferenceResolver;

    @Inject
    private ObservationManager observationManager;

    private GradingConfiguration gradingConfiguration;

    private String identifier;

    /**
     * Retrieve the solr client for storing rankings based on the configuration.
     * If the configuration specifies to use a dedicated core (see {@link GradingConfiguration#hasDedicatedCore()}),
     * then it will use a client based on the current manager identifier, else it will use the default solr core.
     *
     * @return the right solr client for storing rankings.
     * @throws SolrException in case of problem to retrieve the solr client.
     */
    private SolrClient getRankingSolrClient() throws SolrException
    {
        if (this.getGradingConfiguration().hasDedicatedCore()) {
            return this.solr.getClient(this.getIdentifier());
        } else {
            return this.solr.getClient(GradingSolrCoreInitializer.DEFAULT_GRADING_SOLR_CORE);
        }
    }

    private SolrClient getAverageRankSolrClient() throws SolrException
    {
        return this.solr.getClient(AverageGradingSolrCoreInitializer.DEFAULT_AVERAGE_GRADING_SOLR_CORE);
    }

    @Override
    public String getIdentifier()
    {
        return this.identifier;
    }

    @Override
    public void setIdentifer(String identifier)
    {
        this.identifier = identifier;
    }

    @Override
    public int getScale()
    {
        return this.getGradingConfiguration().getScale();
    }

    @Override
    public void setGradingConfiguration(GradingConfiguration configuration)
    {
        this.gradingConfiguration = configuration;
    }

    @Override
    public GradingConfiguration getGradingConfiguration()
    {
        return this.gradingConfiguration;
    }

    private SolrQuery.ORDER getOrder(boolean asc)
    {
        return (asc) ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc;
    }

    private Grading getRankingFromSolrDocument(SolrDocument document)
    {
        String rankingId = this.solrUtils.getId(document);
        String managerId = this.solrUtils.get(GradingQueryField.MANAGER_ID.getFieldName(), document);
        String serializedEntityReference = this.solrUtils.get(GradingQueryField.ENTITY_REFERENCE.getFieldName(),
            document);
        String serializedUserReference = this.solrUtils.get(GradingQueryField.USER_REFERENCE.getFieldName(), document);
        int vote = this.solrUtils.get(GradingQueryField.GRADE.getFieldName(), document);
        Date createdAt = this.solrUtils.get(GradingQueryField.CREATED_DATE.getFieldName(), document);
        Date updatedAt = this.solrUtils.get(GradingQueryField.UPDATED_DATE.getFieldName(), document);
        int scale = this.solrUtils.get(GradingQueryField.SCALE.getFieldName(), document);
        String entityTypeValue = this.solrUtils.get(GradingQueryField.ENTITY_TYPE.getFieldName(), document);

        EntityType entityType = EntityType.valueOf(entityTypeValue);
        UserReference userReference = this.userReferenceResolver.resolve(serializedUserReference);
        EntityReference entityReference = this.entityReferenceResolver.resolve(serializedEntityReference, entityType);

        return new DefaultGrading(rankingId)
            .setReference(entityReference)
            .setUser(userReference)
            .setGrade(vote)
            .setScale(scale)
            .setCreatedAt(createdAt)
            .setUpdatedAt(updatedAt)
            .setManagerId(managerId);
    }

    private List<Grading> getRankingsFromQueryResult(SolrDocumentList documents)
    {
        if (documents != null) {
            return documents.stream().map(this::getRankingFromSolrDocument).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private String mapToQuery(Map<GradingQueryField, Object> originalParameters)
    {
        Map<GradingQueryField, Object> queryParameters = new LinkedHashMap<>(originalParameters);
        queryParameters.put(GradingQueryField.MANAGER_ID, this.getIdentifier());

        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<GradingQueryField, Object>> iterator = queryParameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<GradingQueryField, Object> queryParameter = iterator.next();
            result.append("filter(");
            result.append(queryParameter.getKey().getFieldName());
            result.append(":");

            Object value = queryParameter.getValue();
            if (value instanceof String || value instanceof Date) {
                result.append(solrUtils.toFilterQueryString(value));
            } else if (value instanceof UserReference) {
                result.append(
                    solrUtils.toFilterQueryString(this.userReferenceSerializer.serialize((UserReference) value)));
            } else if (value instanceof EntityReference) {
                result.append(
                    solrUtils.toFilterQueryString(this.entityReferenceSerializer.serialize((EntityReference) value)));
            } else if (value != null) {
                result.append(value);
            }
            result.append(")");
            if (iterator.hasNext()) {
                result.append(" AND ");
            }
        }

        return result.toString();
    }

    private SolrInputDocument getInputDocumentFromRanking(Grading grading)
    {
        SolrInputDocument result = new SolrInputDocument();
        solrUtils.setId(grading.getId(), result);
        solrUtils.set(GradingQueryField.ENTITY_REFERENCE.getFieldName(),
            this.entityReferenceSerializer.serialize(grading.getReference()), result);
        solrUtils.set(GradingQueryField.ENTITY_TYPE.getFieldName(),
            grading.getReference().getType().toString(), result);
        solrUtils.set(GradingQueryField.CREATED_DATE.getFieldName(), grading.getCreatedAt(), result);
        solrUtils.set(GradingQueryField.UPDATED_DATE.getFieldName(), grading.getUpdatedAt(), result);
        solrUtils.set(GradingQueryField.USER_REFERENCE.getFieldName(),
            this.userReferenceSerializer.serialize(grading.getUser()), result);
        solrUtils.set(GradingQueryField.SCALE.getFieldName(), grading.getScale(), result);
        solrUtils.set(GradingQueryField.MANAGER_ID.getFieldName(), grading.getManagerId(), result);
        solrUtils.set(GradingQueryField.GRADE.getFieldName(), grading.getGrade(), result);
        return result;
    }

    private SolrInputDocument getInputDocumentFromAverageRank(AverageGrading averageGrading)
    {
        SolrInputDocument result = new SolrInputDocument();
        solrUtils.setId(averageGrading.getId(), result);
        solrUtils.set(AverageGradingQueryField.ENTITY_REFERENCE.getFieldName(),
            this.entityReferenceSerializer.serialize(averageGrading.getReference()), result);
        solrUtils.set(AverageGradingQueryField.UPDATED_AT.getFieldName(), averageGrading.getUpdatedAt(), result);
        solrUtils.set(AverageGradingQueryField.GRADINGS_NUMBER.getFieldName(),
            averageGrading.getGradingsNumber(), result);
        solrUtils.set(AverageGradingQueryField.SCALE.getFieldName(), averageGrading.getScale(), result);
        solrUtils.set(AverageGradingQueryField.MANAGER_ID.getFieldName(), averageGrading.getManagerId(), result);
        solrUtils.set(AverageGradingQueryField.AVERAGE.getFieldName(), averageGrading.getAverage(), result);
        solrUtils.set(AverageGradingQueryField.ENTITY_TYPE.getFieldName(),
            averageGrading.getReference().getType(), result);
        return result;
    }

    private Optional<Grading> retrieveExistingRanking(EntityReference rankedEntity, UserReference voter)
        throws GradingException
    {
        String serializedEntity = this.entityReferenceSerializer.serialize(rankedEntity);
        String serializedUserReference = this.userReferenceSerializer.serialize(voter);
        Map<GradingQueryField, Object> queryMap = new LinkedHashMap<>();
        queryMap.put(GradingQueryField.ENTITY_REFERENCE, serializedEntity);
        queryMap.put(GradingQueryField.ENTITY_TYPE, rankedEntity.getType());
        queryMap.put(GradingQueryField.USER_REFERENCE, serializedUserReference);

        List<Grading> gradings = this.getGradings(queryMap, 0, 1, GradingQueryField.CREATED_DATE, true);
        if (gradings.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(gradings.get(0));
        }
    }

    @Override
    public Grading saveGrading(EntityReference reference, UserReference user, int grade)
        throws GradingException
    {
        // If the vote is outside the scope of the scale, we throw an exception immediately.
        if (grade < 0 || grade > this.getScale()) {
            throw new GradingException(String.format("The vote [%s] is out of scale [%s] for [%s] ranking manager.",
                grade, this.getScale(), this.getIdentifier()));
        }

        // Check if a vote for the same entity by the same user and on the same manager already exists.
        Optional<Grading> existingRanking = this.retrieveExistingRanking(reference, user);

        boolean storeAverage = this.getGradingConfiguration().storeAverage();
        Event event = null;
        Grading result = null;
        AverageGrading averageGrading = null;
        Event averageEvent = null;

        // It's the first vote for the tuple entity, user, manager.
        if (!existingRanking.isPresent()) {

            // We only store the vote if it's not 0 or if the configuration allows to store 0
            if (grade != 0 || this.getGradingConfiguration().storeZero()) {
                result = new DefaultGrading(UUID.randomUUID().toString())
                    .setManagerId(this.getIdentifier())
                    .setReference(reference)
                    .setCreatedAt(new Date())
                    .setUpdatedAt(new Date())
                    .setGrade(grade)
                    .setScale(this.getScale())
                    .setUser(user);

                // it's a vote creation
                event = new CreatedGradingEvent();

                if (storeAverage) {
                    averageGrading = this.getAverageGrading(reference);
                    averageEvent =
                        new UpdatedAverageGradingEvent(averageGrading.getAverage(), averageGrading.getGradingsNumber());
                    averageGrading.addGrading(grade);
                }
            }

        // There was already a vote with the same information
        } else {
            Grading oldGrading = existingRanking.get();

            // If the vote is not 0 or if we store zero, we just modify the existing vote
            if (grade != 0) {
                result = new DefaultGrading(oldGrading)
                    .setUpdatedAt(new Date())
                    .setGrade(grade);

                // It's an update of a vote
                event = new UpdatedGradingEvent(oldGrading.getGrade());
                if (storeAverage) {
                    averageGrading = this.getAverageGrading(reference);
                    averageEvent =
                        new UpdatedAverageGradingEvent(averageGrading.getAverage(), averageGrading.getGradingsNumber());
                    averageGrading.updateGrading(oldGrading.getGrade(), grade);
                }
            // Else we remove it.
            } else if (this.gradingConfiguration.storeZero()) {
                this.removeGrading(oldGrading.getId());
            }
        }

        // If there's a vote to store (all cases except if the vote is 0 and we don't store it)
        if (result != null) {
            SolrInputDocument solrInputDocument = this.getInputDocumentFromRanking(result);
            try {
                // Store the new document in Solr
                this.getRankingSolrClient().add(solrInputDocument);
                this.getRankingSolrClient().commit();

                // Send the appropriate notification
                this.observationManager.notify(event, this.getIdentifier(), result);

                // If we store the average, we also compute the new informations for it.
                if (storeAverage) {
                    this.getAverageRankSolrClient().add(this.getInputDocumentFromAverageRank(averageGrading));
                    this.getAverageRankSolrClient().commit();
                    this.observationManager.notify(averageEvent, this.getIdentifier(), averageGrading);
                }
            } catch (SolrServerException | IOException | SolrException e) {
                throw new GradingException(
                    String.format("Error when storing rank information for entity [%s] with user [%s].",
                        reference, user), e);
            }
        }
        return result;
    }

    @Override
    public List<Grading> getGradings(Map<GradingQueryField, Object> queryParameters, int offset, int limit,
        GradingQueryField orderBy, boolean asc) throws GradingException
    {
        SolrQuery solrQuery = new SolrQuery()
            .addFilterQuery(this.mapToQuery(queryParameters))
            .setStart(offset)
            .setRows(limit)
            .setSort(orderBy.getFieldName(), this.getOrder(asc));

        try {
            QueryResponse query = this.getRankingSolrClient().query(solrQuery);
            return this.getRankingsFromQueryResult(query.getResults());
        } catch (SolrServerException | IOException | SolrException e) {
            throw new GradingException("Error while trying to get rankings", e);
        }
    }

    @Override
    public long countGradings(Map<GradingQueryField, Object> queryParameters) throws GradingException
    {
        SolrQuery solrQuery = new SolrQuery()
            .addFilterQuery(this.mapToQuery(queryParameters))
            .setStart(0)
            .setRows(0);

        try {
            QueryResponse query = this.getRankingSolrClient().query(solrQuery);
            return query.getResults().getNumFound();
        } catch (SolrServerException | IOException | SolrException e) {
            throw new GradingException("Error while trying to get count of rankings", e);
        }
    }

    @Override
    public boolean removeGrading(String gradingIdentifier) throws GradingException
    {
        Map<GradingQueryField, Object> queryMap = Collections
            .singletonMap(GradingQueryField.IDENTIFIER, gradingIdentifier);

        List<Grading> gradings = this.getGradings(queryMap, 0, 1, GradingQueryField.CREATED_DATE, true);
        if (!gradings.isEmpty()) {
            try {
                this.getRankingSolrClient().deleteById(gradingIdentifier);
                this.getRankingSolrClient().commit();
                Grading grading = gradings.get(0);
                this.observationManager.notify(new DeletedGradingEvent(), this.getIdentifier(), grading);
                if (this.getGradingConfiguration().storeAverage()) {
                    AverageGrading averageGrading = getAverageGrading(grading.getReference());
                    UpdatedAverageGradingEvent event = new UpdatedAverageGradingEvent(averageGrading.getAverage(),
                        averageGrading.getGradingsNumber());
                    averageGrading.removeGrading(grading.getGrade());
                    this.getAverageRankSolrClient().add(this.getInputDocumentFromAverageRank(averageGrading));
                    this.getAverageRankSolrClient().commit();
                    this.observationManager.notify(event, this.getIdentifier(), averageGrading);
                }
                return true;
            } catch (SolrServerException | IOException | SolrException e) {
                throw new GradingException("Error while removing ranking.", e);
            }
        } else {
            return false;
        }
    }

    @Override
    public AverageGrading getAverageGrading(EntityReference entityReference) throws GradingException
    {
        SolrQuery solrQuery = new SolrQuery()
            .addFilterQuery(String.format("filter(%s:%s) AND filter(%s:%s) AND filter(%s:%s)",
                AverageGradingQueryField.MANAGER_ID.getFieldName(), solrUtils.toFilterQueryString(this.getIdentifier()),
                AverageGradingQueryField.ENTITY_REFERENCE.getFieldName(),
                solrUtils.toFilterQueryString(this.entityReferenceSerializer.serialize(entityReference)),
                AverageGradingQueryField.ENTITY_TYPE.getFieldName(),
                solrUtils.toFilterQueryString(entityReference.getType())))
            .setStart(0)
            .setRows(1)
            .setSort(AverageGradingQueryField.UPDATED_AT.getFieldName(), this.getOrder(true));

        try {
            QueryResponse query = this.getAverageRankSolrClient().query(solrQuery);
            AverageGrading result;
            if (!query.getResults().isEmpty()) {
                SolrDocument solrDocument = query.getResults().get(0);

                result = new DefaultAverageGrading(solrUtils.getId(solrDocument))
                    .setManagerId(solrUtils.get(AverageGradingQueryField.MANAGER_ID.getFieldName(), solrDocument))
                    .setAverage(solrUtils.get(AverageGradingQueryField.AVERAGE.getFieldName(), solrDocument))
                    .setReference(entityReference)
                    .setGradingsNumber(solrUtils.get(AverageGradingQueryField.GRADINGS_NUMBER.getFieldName(),
                        solrDocument))
                    .setScale(solrUtils.get(AverageGradingQueryField.SCALE.getFieldName(), solrDocument))
                    .setUpdatedAt(solrUtils.get(AverageGradingQueryField.UPDATED_AT.getFieldName(), solrDocument));
            } else {
                result = new DefaultAverageGrading(UUID.randomUUID().toString())
                    .setManagerId(this.getIdentifier())
                    .setScale(this.getScale())
                    .setReference(entityReference)
                    .setAverage(0)
                    .setGradingsNumber(0);
            }
            return result;
        } catch (SolrServerException | IOException | SolrException e) {
            throw new GradingException("Error while trying to get average ranking value.", e);
        }
    }
}
