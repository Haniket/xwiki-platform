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

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.grading.AverageGrading;
import org.xwiki.text.XWikiToStringBuilder;

/**
 * Default implementation of {@link AverageGrading}.
 * This implementation provides a builder API.
 *
 * @version $Id$
 * @since 12.8RC1
 */
public class DefaultAverageGrading implements AverageGrading
{
    private String identifier;
    private String managerId;
    private EntityReference reference;
    private double average;
    private long gradingsNumber;
    private int scale;
    private Date updatedAt;

    /**
     * Default constructor with identifier.
     *
     * @param identifier unique identifier of the average.
     */
    public DefaultAverageGrading(String identifier)
    {
        this.identifier = identifier;
        this.updatedAt = new Date();
    }

    /**
     * Constructor that allows cloning an existing average rank.
     *
     * @param averageGrading the already existing object to clone.
     */
    public DefaultAverageGrading(AverageGrading averageGrading)
    {
        this.identifier = averageGrading.getId();
        this.managerId = averageGrading.getManagerId();
        this.reference = averageGrading.getReference();
        this.average = averageGrading.getAverage();
        this.gradingsNumber = averageGrading.getGradingsNumber();
        this.scale = averageGrading.getScale();
        this.updatedAt = averageGrading.getUpdatedAt();
    }

    @Override
    public AverageGrading updateGrading(int oldGrading, int newGrading)
    {
        double newTotal = (this.average * this.gradingsNumber) - oldGrading + newGrading;
        this.average = newTotal / this.gradingsNumber;
        this.updatedAt = new Date();
        return this;
    }

    @Override
    public AverageGrading removeGrading(int grading)
    {
        double newTotal = (this.average * this.gradingsNumber) - grading;
        this.gradingsNumber--;
        this.average = newTotal / this.gradingsNumber;
        this.updatedAt = new Date();
        return this;
    }

    @Override
    public AverageGrading addGrading(int grading)
    {
        double newTotal = (this.average * this.gradingsNumber) + grading;
        this.gradingsNumber++;
        this.average = newTotal / this.gradingsNumber;
        this.updatedAt = new Date();
        return this;
    }

    /**
     * @param managerId the manager identifier to set.
     * @return the current instance.
     */
    public DefaultAverageGrading setManagerId(String managerId)
    {
        this.managerId = managerId;
        return this;
    }

    /**
     * @param reference the reference of the element being graded.
     * @return the current instance.
     */
    public DefaultAverageGrading setReference(EntityReference reference)
    {
        this.reference = reference;
        return this;
    }

    /**
     * @param average the average value.
     * @return the current instance.
     */
    public DefaultAverageGrading setAverage(double average)
    {
        this.average = average;
        return this;
    }

    /**
     * @param gradingsNumber the number of graded elements.
     * @return the current instance.
     */
    public DefaultAverageGrading setGradingsNumber(long gradingsNumber)
    {
        this.gradingsNumber = gradingsNumber;
        return this;
    }

    /**
     * @param scale the scale used for grading elements.
     * @return the current instance.
     */
    public DefaultAverageGrading setScale(int scale)
    {
        this.scale = scale;
        return this;
    }

    /**
     * @param updatedAt the date of last update.
     * @return the current instance.
     */
    public DefaultAverageGrading setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public String getId()
    {
        return this.identifier;
    }

    @Override
    public String getManagerId()
    {
        return this.managerId;
    }

    @Override
    public EntityReference getReference()
    {
        return this.reference;
    }

    @Override
    public double getAverage()
    {
        return this.average;
    }

    @Override
    public long getGradingsNumber()
    {
        return this.gradingsNumber;
    }

    @Override
    public int getScale()
    {
        return this.scale;
    }

    @Override
    public Date getUpdatedAt()
    {
        return updatedAt;
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

        DefaultAverageGrading that = (DefaultAverageGrading) o;

        return new EqualsBuilder()
            .append(average, that.average)
            .append(gradingsNumber, that.gradingsNumber)
            .append(scale, that.scale)
            .append(identifier, that.identifier)
            .append(managerId, that.managerId)
            .append(reference, that.reference)
            .append(updatedAt, that.updatedAt)
            .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 67)
            .append(identifier)
            .append(managerId)
            .append(reference)
            .append(average)
            .append(gradingsNumber)
            .append(scale)
            .append(updatedAt)
            .toHashCode();
    }

    @Override
    public String toString()
    {
        return new XWikiToStringBuilder(this)
            .append("identifier", identifier)
            .append("managerId", managerId)
            .append("rankedElement", reference)
            .append("averageRank", average)
            .append("rankingNumber", gradingsNumber)
            .append("scale", scale)
            .append("updatedAt", updatedAt)
            .toString();
    }
}
