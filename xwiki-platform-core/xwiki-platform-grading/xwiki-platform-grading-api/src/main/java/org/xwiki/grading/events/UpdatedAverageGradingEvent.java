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
package org.xwiki.grading.events;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.xwiki.observation.event.Event;
import org.xwiki.grading.AverageGrading;
import org.xwiki.grading.GradingManager;
import org.xwiki.stability.Unstable;

/**
 * Event sent whenever an {@link AverageGrading} is updated.
 * The event is sent with the following informations:
 *   - source: the identifier of the {@link GradingManager}
 *   - data: the {@link AverageGrading} updated.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@Unstable
public class UpdatedAverageGradingEvent implements Event
{
    private double oldAverage;
    private long oldGradingsNumber;

    /**
     * Default constructor.
     *
     * @param oldAverage the old average value, before the update.
     * @param oldGradingsNumber the old number of gradings, before the update.
     */
    public UpdatedAverageGradingEvent(double oldAverage, long oldGradingsNumber)
    {
        this.oldAverage = oldAverage;
        this.oldGradingsNumber = oldGradingsNumber;
    }

    /**
     * @return the old average value, before the update.
     */
    public double getOldAverage()
    {
        return oldAverage;
    }

    /**
     * @return the old number of gradings, before the update.
     */
    public long getOldGradingsNumber()
    {
        return oldGradingsNumber;
    }

    @Override
    public boolean matches(Object otherEvent)
    {
        return otherEvent instanceof UpdatedAverageGradingEvent;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UpdatedAverageGradingEvent that = (UpdatedAverageGradingEvent) o;

        return new EqualsBuilder()
            .append(oldAverage, that.oldAverage)
            .append(oldGradingsNumber, that.oldGradingsNumber)
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
            .append(oldAverage)
            .append(oldGradingsNumber)
            .toHashCode();
    }
}
