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

import java.util.Date;

import org.xwiki.model.reference.EntityReference;
import org.xwiki.stability.Unstable;

/**
 * General interface to provide information about average grading notation.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@Unstable
public interface AverageGrading
{
    /**
     * @return the identifier of this average grading data.
     */
    String getId();

    /**
     * @return the identifier of the manager who handles this average grading data.
     */
    String getManagerId();

    /**
     * @return the reference of the element this average grading is for.
     */
    EntityReference getReference();

    /**
     * @return the actual average grading.
     */
    double getAverage();

    /**
     * @return the total number of gradings used to compute this average.
     */
    long getGradingsNumber();

    /**
     * @return the upper bound scale of the gradings.
     */
    int getScale();

    /**
     * @return the date of the last modification of this average.
     */
    Date getUpdatedAt();

    /**
     * Update the average grading by performing a change in an existing grading.
     *
     * @param oldGrading the old grading value to be modified.
     * @param newGrading the new grading value to be applied.
     * @return the current instance modified.
     */
    AverageGrading updateGrading(int oldGrading, int newGrading);

    /**
     * Update the average grading by removing the given grading.
     *
     * @param grading the old grading to be removed.
     * @return the current instance modified.
     */
    AverageGrading removeGrading(int grading);

    /**
     * Update the average grading by adding a new grading.
     *
     * @param grading the new grading to be taken into account.
     * @return the current instance modified.
     */
    AverageGrading addGrading(int grading);
}
