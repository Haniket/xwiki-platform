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
package com.xpn.xwiki.plugin.activitystream.plugin;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.plugin.XWikiDefaultPlugin;
import com.xpn.xwiki.plugin.XWikiPluginInterface;
import com.xpn.xwiki.plugin.activitystream.api.ActivityStream;
import com.xpn.xwiki.plugin.activitystream.impl.ActivityStreamImpl;

/**
 * Plug-in for for managing streams of activity events
 * 
 * @see ActivityStream
 * @version $Id: $
 */
public class ActivityStreamPlugin extends XWikiDefaultPlugin
{
    /**
     * We should user inversion of control instead
     */
    private ActivityStream activityStream;

    public static final String PLUGIN_NAME = "activitystream";

    /**
     * @see XWikiDefaultPlugin#XWikiDefaultPlugin(String,String,com.xpn.xwiki.XWikiContext)
     */
    public ActivityStreamPlugin(String name, String className, XWikiContext context)
    {
        super(name, className, context);
        setActivityStream(new ActivityStreamImpl());
    }

    /**
     * {@inheritDoc}
     * 
     * @see XWikiDefaultPlugin#getName()
     */
    public String getName()
    {
        return PLUGIN_NAME;
    }

    /**
     * {@inheritDoc}
     * 
     * @see XWikiDefaultPlugin#getPluginApi
     */
    public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context)
    {
        return new ActivityStreamPluginApi((ActivityStreamPlugin) plugin, context);
    }

    /**
     * @return The {@link ActivityStream} component used in behind by this plug-in instance
     */
    public ActivityStream getActivityStream()
    {
        return activityStream;
    }

    /**
     * @param activityStream The {@link ActivityStream} component to be used
     */
    public void setActivityStream(ActivityStream activityStream)
    {
        this.activityStream = activityStream;
    }

    /**
     * Get a preference for the activitystream from the XWiki configuration.
     * 
     * @param preference Name of the preference to get the value from
     * @param defaultValue Default value if the preference is not found in the configuration
     * @param context the XWiki context
     * @return value for the given preference
     */
    public String getActivityStreamPreference(String preference, String defaultValue, XWikiContext context)
    {
        String preferencePrefix = "xwiki.plugin.activitystream.";
        return context.getWiki().getXWikiPreference(preferencePrefix + preference, defaultValue, context);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see XWikiDefaultPlugin#init(XWikiContext)
     */
    public void init(XWikiContext context)
    {
        super.init(context);
        try {
            activityStream.initClasses(context);
        } catch (Exception e) {
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see XWikiDefaultPlugin#virtualInit(XWikiContext)
     */
    public void virtualInit(XWikiContext context)
    {
        super.virtualInit(context);
        try {
            // activityStream.initClasses(context);
        } catch (Exception e) {
        }
    }
}
