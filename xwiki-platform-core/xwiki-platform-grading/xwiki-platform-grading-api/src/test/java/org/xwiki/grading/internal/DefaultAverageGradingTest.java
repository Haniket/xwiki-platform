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

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.grading.AverageGrading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link AverageGrading}.
 *
 * @version $Id$
 * @since 12.8RC1
 */
public class DefaultAverageGradingTest
{
    @Test
    void simpleConstructor()
    {
        Date currentDate = new Date();
        DefaultAverageGrading defaultAverageRank = new DefaultAverageGrading("myId");
        assertEquals("myId", defaultAverageRank.getId());
        assertEquals(0, defaultAverageRank.getGradingsNumber());
        assertEquals(0, defaultAverageRank.getAverage(), 0);
        assertTrue(currentDate.toInstant()
            .isAfter(defaultAverageRank.getUpdatedAt().toInstant().minus(1, ChronoUnit.MINUTES)));
        assertNotEquals(new Date(0), defaultAverageRank.getUpdatedAt());
        assertNotNull(defaultAverageRank.getUpdatedAt());
    }

    @Test
    void cloneConstructor()
    {
        AverageGrading customRank = new AverageGrading()
        {
            @Override
            public String getId()
            {
                return "someId";
            }

            @Override
            public String getManagerId()
            {
                return "myManagerId";
            }

            @Override
            public EntityReference getReference()
            {
                return new EntityReference("Foobar", EntityType.ATTACHMENT);
            }

            @Override
            public double getAverage()
            {
                return 0.23;
            }

            @Override
            public long getGradingsNumber()
            {
                return 1343;
            }

            @Override
            public int getScale()
            {
                return 3;
            }

            @Override
            public Date getUpdatedAt()
            {
                return new Date(24);
            }

            @Override
            public AverageGrading updateGrading(int oldGrading, int newGrading)
            {
                return this;
            }

            @Override
            public AverageGrading removeGrading(int grading)
            {
                return this;
            }

            @Override
            public AverageGrading addGrading(int grading)
            {
                return this;
            }
        };

        DefaultAverageGrading defaultAverageRank = new DefaultAverageGrading(customRank);
        assertNotEquals(defaultAverageRank, customRank);
        assertEquals("someId", defaultAverageRank.getId());
        assertEquals("myManagerId", defaultAverageRank.getManagerId());
        assertEquals(new EntityReference("Foobar", EntityType.ATTACHMENT), defaultAverageRank.getReference());
        assertEquals(EntityType.ATTACHMENT, defaultAverageRank.getReference().getType());
        assertEquals(0.23, defaultAverageRank.getAverage(), 0);
        assertEquals(1343, defaultAverageRank.getGradingsNumber());
        assertEquals(3, defaultAverageRank.getScale());
        assertEquals(new Date(24), defaultAverageRank.getUpdatedAt());
        assertEquals(defaultAverageRank, new DefaultAverageGrading(defaultAverageRank));
    }

    @Test
    void addVote()
    {
        DefaultAverageGrading defaultAverageRank = new DefaultAverageGrading("myId");
        assertEquals("myId", defaultAverageRank.getId());
        assertEquals(0, defaultAverageRank.getGradingsNumber());
        assertEquals(0, defaultAverageRank.getAverage(), 0);

        Date beforeFirstUpdate = new Date();
        assertNotEquals(new Date(0), defaultAverageRank.getUpdatedAt());
        assertNotNull(defaultAverageRank.getUpdatedAt());
        assertTrue(beforeFirstUpdate.toInstant()
            .isAfter(defaultAverageRank.getUpdatedAt().toInstant().minus(1, ChronoUnit.MINUTES)));

        defaultAverageRank.addGrading(4);
        assertEquals(4, defaultAverageRank.getAverage(), 0);
        assertEquals(1, defaultAverageRank.getGradingsNumber());
        assertNotEquals(new Date(0), defaultAverageRank.getUpdatedAt());
        assertNotNull(defaultAverageRank.getUpdatedAt());
        assertTrue(beforeFirstUpdate.toInstant()
            .isAfter(defaultAverageRank.getUpdatedAt().toInstant().minus(1, ChronoUnit.MINUTES)));

        defaultAverageRank.addGrading(1);
        defaultAverageRank.addGrading(3);
        defaultAverageRank.addGrading(0);
        defaultAverageRank.addGrading(3);
        defaultAverageRank.addGrading(2);
        assertEquals(6, defaultAverageRank.getGradingsNumber());
        assertEquals(2.166667, defaultAverageRank.getAverage(), 0.000001);
    }

    @Test
    void removeVote()
    {
        DefaultAverageGrading defaultAverageRank = new DefaultAverageGrading("myId")
            .setAverage(13.0 / 6)
            .setGradingsNumber(6);
        assertEquals("myId", defaultAverageRank.getId());
        assertEquals(6, defaultAverageRank.getGradingsNumber());
        assertEquals(2.166667, defaultAverageRank.getAverage(), 0.000001);

        Date beforeFirstUpdate = new Date();
        assertNotEquals(new Date(0), defaultAverageRank.getUpdatedAt());
        assertNotNull(defaultAverageRank.getUpdatedAt());
        assertTrue(beforeFirstUpdate.toInstant()
            .isAfter(defaultAverageRank.getUpdatedAt().toInstant().minus(1, ChronoUnit.MINUTES)));

        defaultAverageRank.removeGrading(0);
        assertEquals(2.6, defaultAverageRank.getAverage(), 0);
        assertEquals(5, defaultAverageRank.getGradingsNumber());
        assertNotEquals(new Date(0), defaultAverageRank.getUpdatedAt());
        assertNotNull(defaultAverageRank.getUpdatedAt());
        assertTrue(beforeFirstUpdate.toInstant()
            .isAfter(defaultAverageRank.getUpdatedAt().toInstant().minus(1, ChronoUnit.MINUTES)));
    }

    @Test
    void updateVote()
    {
        DefaultAverageGrading defaultAverageRank = new DefaultAverageGrading("myId")
            .setAverage(13.0 / 6)
            .setGradingsNumber(6);
        assertEquals("myId", defaultAverageRank.getId());
        assertEquals(6, defaultAverageRank.getGradingsNumber());
        assertEquals(2.166667, defaultAverageRank.getAverage(), 0.000001);

        Date beforeFirstUpdate = new Date();
        assertNotEquals(new Date(0), defaultAverageRank.getUpdatedAt());
        assertNotNull(defaultAverageRank.getUpdatedAt());
        assertTrue(beforeFirstUpdate.toInstant()
            .isAfter(defaultAverageRank.getUpdatedAt().toInstant().minus(1, ChronoUnit.MINUTES)));

        defaultAverageRank.updateGrading(0, 5);
        assertEquals(3, defaultAverageRank.getAverage(), 0);
        assertEquals(6, defaultAverageRank.getGradingsNumber());
        assertNotEquals(new Date(0), defaultAverageRank.getUpdatedAt());
        assertNotNull(defaultAverageRank.getUpdatedAt());
        assertTrue(beforeFirstUpdate.toInstant()
            .isAfter(defaultAverageRank.getUpdatedAt().toInstant().minus(1, ChronoUnit.MINUTES)));
    }
}
