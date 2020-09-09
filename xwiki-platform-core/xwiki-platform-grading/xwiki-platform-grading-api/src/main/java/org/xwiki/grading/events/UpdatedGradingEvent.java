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
import org.xwiki.grading.Grading;
import org.xwiki.grading.GradingManager;
import org.xwiki.stability.Unstable;

/**
 * Event sent whenever a {@link Grading} is updated.
 * The event is sent with the following informations:
 *   - source: the identifier of the {@link GradingManager}
 *   - data: the {@link Grading} updated.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@Unstable
public class UpdatedGradingEvent implements Event
{
    private int oldGrading;

    /**
     * Default constructor.
     *
     * @param oldGrading the grading before the update.
     */
    public UpdatedGradingEvent(int oldGrading)
    {
        this.oldGrading = oldGrading;
    }

    /**
     * @return the old grading, before the update.
     */
    public int getOldGrading()
    {
        return oldGrading;
    }

    @Override
    public boolean matches(Object otherEvent)
    {
        return otherEvent instanceof UpdatedGradingEvent;
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

        UpdatedGradingEvent that = (UpdatedGradingEvent) o;

        return new EqualsBuilder()
            .append(oldGrading, that.oldGrading)
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
            .append(oldGrading)
            .toHashCode();
    }
}
