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

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.search.solr.AbstractSolrCoreInitializer;
import org.xwiki.search.solr.SolrException;

/**
 * Solr Core initializer for Average grading data.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@Component
@Singleton
@Named(AverageGradingSolrCoreInitializer.DEFAULT_AVERAGE_GRADING_SOLR_CORE)
public class AverageGradingSolrCoreInitializer extends AbstractSolrCoreInitializer
{
    /**
     * Name of the solr core.
     */
    public static final String DEFAULT_AVERAGE_GRADING_SOLR_CORE = "averageGrading";

    /**
     * Fields to be used for Average grading data.
     */
    public enum AverageGradingQueryField
    {
        /**
         * Field for storing manager identifier.
         */
        MANAGER_ID("managerId"),

        /**
         * Field for storing ranked element serialized reference.
         */
        ENTITY_REFERENCE("reference"),

        /**
         * Field for storing the entity type of the serialized reference.
         */
        ENTITY_TYPE("entityType"),

        /**
         * Field for storing the actual average of the rank.
         */
        AVERAGE("average"),

        /**
         * Field for storing the total number of votes.
         */
        GRADINGS_NUMBER("gradingsNumber"),

        /**
         * Field for storing the upper bound scale of the ranking manager.
         */
        SCALE("scale"),

        /**
         * Field for storing the latest updated date.
         */
        UPDATED_AT("updatedAt");

        private String fieldName;

        /**
         * Default constructor.
         *
         * @param fieldName actual name of the field.
         */
        AverageGradingQueryField(String fieldName)
        {
            this.fieldName = fieldName;
        }

        /**
         * @return the actual name of the field to be used in queries.
         */
        public String getFieldName()
        {
            return this.fieldName;
        }
    }

    private static final long CURRENT_VERSION = 120800000;

    @Override
    protected void createSchema() throws SolrException
    {
        this.addStringField(AverageGradingQueryField.MANAGER_ID.getFieldName(), false, false);
        this.addStringField(AverageGradingQueryField.ENTITY_REFERENCE.getFieldName(), false, false);
        this.addStringField(AverageGradingQueryField.ENTITY_TYPE.getFieldName(), false, false);
        this.addPDoubleField(AverageGradingQueryField.AVERAGE.getFieldName(), false, false);
        this.addPLongField(AverageGradingQueryField.GRADINGS_NUMBER.getFieldName(), false, false);
        this.addPIntField(AverageGradingQueryField.SCALE.getFieldName(), false, false);
        this.addPDateField(AverageGradingQueryField.UPDATED_AT.getFieldName(), false, false);
    }

    @Override
    protected void migrateSchema(long cversion) throws SolrException
    {
        // No migration yet.
    }

    @Override
    protected long getVersion()
    {
        return CURRENT_VERSION;
    }
}
