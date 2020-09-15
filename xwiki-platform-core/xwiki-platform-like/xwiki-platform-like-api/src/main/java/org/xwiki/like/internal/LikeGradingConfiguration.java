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
package org.xwiki.like.internal;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.grading.GradingConfiguration;

/**
 * Default {@link GradingConfiguration} for Likes.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@Component
@Singleton
@Named(LikeGradingConfiguration.RANKING_MANAGER_HINT)
public class LikeGradingConfiguration implements GradingConfiguration
{
    /**
     * Default hint for Ranking Manager.
     */
    public static final String RANKING_MANAGER_HINT = "like";

    @Override
    public boolean storeZero()
    {
        return false;
    }

    @Override
    public int getScale()
    {
        return 1;
    }

    @Override
    public boolean hasDedicatedCore()
    {
        return false;
    }

    @Override
    public boolean storeAverage()
    {
        return false;
    }

    @Override
    public String getStorageHint()
    {
        return "solr";
    }
}
