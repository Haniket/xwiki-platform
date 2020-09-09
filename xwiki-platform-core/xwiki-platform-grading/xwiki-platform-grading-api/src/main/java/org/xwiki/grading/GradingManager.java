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
package org.xwiki.grading;

import java.util.List;
import java.util.Map;

import org.xwiki.component.annotation.Role;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.grading.events.CreatedGradingEvent;
import org.xwiki.grading.events.UpdatedGradingEvent;
import org.xwiki.stability.Unstable;
import org.xwiki.user.UserReference;

/**
 * Manager for handling Grading operations.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@Role
@Unstable
public interface GradingManager
{
    /**
     * The fields to be used for performing queries on Gradings.
     */
    enum GradingQueryField
    {
        IDENTIFIER("id"),
        ENTITY_REFERENCE("reference"),
        ENTITY_TYPE("entityType"),
        USER_REFERENCE("user"),
        GRADE("grade"),
        CREATED_DATE("createdAt"),
        UPDATED_DATE("updatedAt"),
        MANAGER_ID("managerId"),
        SCALE("scale");

        private final String fieldName;

        GradingQueryField(String fieldName)
        {
            this.fieldName = fieldName;
        }

        public String getFieldName()
        {
            return this.fieldName;
        }
    }

    /**
     * @return the identifier of the current manager.
     */
    String getIdentifier();

    /**
     * Allows to set the identifier of the manager.
     * This method should only be used when creating the manager in a {@link GradingManagerFactory}.
     *
     * @param identifier the identifier to be set.
     */
    void setIdentifer(String identifier);

    /**
     * @return the upper bound of the scale used by this manager for grading.
     */
    int getScale();

    /**
     * Allows to set the configuration of the manager.
     * This method should only be used when creating the manager in a {@link GradingManagerFactory}.
     *
     * @param configuration the configuration to be set.
     */
    void setGradingConfiguration(GradingConfiguration configuration);

    /**
     * @return the configuration used by this manager.
     */
    GradingConfiguration getGradingConfiguration();

    /**
     * Save and return a {@link Grading} information.
     * If an existing rank has already been saved by the same user on the same reference, then this method updates the
     * existing value.
     * This method should check that the given grade matches the scale of the manager.
     * It should also take into account the {@link GradingConfiguration#storeZero()} configuration to handle case when
     * the grade is equal to 0. The method returns null if the grade is equal to 0 and the configuration doesn't allow
     * to store it, but it might perform storage side effect (such as removing a previous {@link Grading} information).
     * This method also handles the computation of {@link AverageGrading} if the
     * {@link GradingConfiguration#storeAverage()} configuration is set to true.
     * Note that this method should also handle sending the appropriate
     * {@link CreatedGradingEvent} and {@link UpdatedGradingEvent}.
     *
     * @param reference the entity for which to save a grading value.
     * @param user the user who performs the rank.
     * @param grade the actual grade to be saved.
     * @return the saved grading or null if none has been saved.
     * @throws GradingException in case of problem for saving the ranking.
     */
    Grading saveGrading(EntityReference reference, UserReference user, int grade) throws GradingException;

    /**
     * Retrieve the list of gradings based on the given query parameters.
     * Only exact matching can be used right now for the given query parameters. It's possible to provide some
     * objects as query parameters: some specific treatment can be apply depending on the type of the objects, but for
     * most type we're just relying on {@code String.valueOf(Object)}. Only the rankings of the current manager are
     * retrieved even if the store is shared.
     *
     * @param queryParameters the map of parameters to rely on for query the gradings.
     * @param offset the offset where to start getting results.
     * @param limit the limit number of results to retrieve.
     * @param orderBy the field to use for sorting the results.
     * @param asc if {@code true}, use ascending order for sorting, else use descending order.
     * @return a list containing at most {@code limit} gradings results.
     * @throws GradingException in case of problem for querying the gradings.
     */
    List<Grading> getGradings(Map<GradingQueryField, Object> queryParameters,
        int offset, int limit, GradingQueryField orderBy, boolean asc) throws GradingException;

    /**
     * Retrieve the number of gradings matching the given parameters but without retrieving them directly.
     * Only exact matching can be used right now for the given query parameters. It's possible to provide some
     * objects as query parameters: some specific treatment can be apply depending on the type of the objects, but for
     * most type we're just relying on {@code String.valueOf(Object)}. Only the gradings of the current manager are
     * retrieved even if the store is shared.
     *
     * @param queryParameters the map of parameters to rely on for query the gradings.
     * @return the total number of gradings matching the query parameters.
     * @throws GradingException in case of problem during the query.
     */
    long countGradings(Map<GradingQueryField, Object> queryParameters) throws GradingException;

    /**
     * Remove a grading based on its identifier.
     * This method also performs an update of the {@link AverageGrading} if the
     * {@link GradingConfiguration#storeAverage()} is enabled.
     *
     * @param gradingIdentifier the ranking identifier to remove.
     * @return {@code true} if a grading is deleted, {@code false} if no grading with the given identifier can be found.
     * @throws GradingException in case of problem during the query.
     */
    boolean removeGrading(String gradingIdentifier) throws GradingException;

    /**
     * Retrieve the average grading information of the given reference.
     *
     * @param entityReference the reference for which to retrieve the average grading information.
     * @return the average grading data corresponding to the given reference.
     * @throws GradingException in case of problem during the query.
     */
    AverageGrading getAverageGrading(EntityReference entityReference) throws GradingException;
}
