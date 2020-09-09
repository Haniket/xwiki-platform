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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.descriptor.ComponentDescriptor;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.manager.ComponentRepositoryException;
import org.xwiki.grading.GradingConfiguration;
import org.xwiki.grading.GradingException;
import org.xwiki.grading.GradingManager;
import org.xwiki.grading.GradingManagerFactory;

/**
 * Default implementation of {@link GradingManagerFactory}.
 * This implementation performs the following for getting a {@link GradingManager}:
 *   1. it looks in the context component manager for a GradingManager with the given hint, and returns it immediately
 *      if there is one. If there is not, it performs the following steps.
 *   2. it retrieves the {@link GradingConfiguration} based on the given hint, and fallback on the default one if it
 *      cannot find it.
 *   3. it retrieves an instance of a {@link GradingManager} based on the {@link GradingConfiguration#getStorageHint()}
 *   4. it set in the newly instance of {@link GradingManager} some information such as its identifiers
 *      and configuration
 *   5. it creates a new {@link ComponentDescriptor} by copying the descriptor of the retrieved
 *      {@link GradingManager}, modifies it to specify the asked hint and by changing the instatiation strategy
 *      so that it's only instantiated once
 *   6. finally it registers the new component descriptor it in the current component manager so that in next request of
 *      a {@link GradingManager} with the same hint, the retrieved instance will be returned.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@Component
@Singleton
public class DefaultGradingManagerFactory implements GradingManagerFactory
{
    @Inject
    @Named("context")
    private ComponentManager contextComponentManager;

    @Inject
    private ComponentManager currentComponentManager;

    private GradingConfiguration getGradingConfiguration(String hint) throws ComponentLookupException
    {
        GradingConfiguration result;
        if (this.contextComponentManager.hasComponent(GradingConfiguration.class, hint)) {
            result = this.contextComponentManager.getInstance(GradingConfiguration.class, hint);
        } else {
            result = this.contextComponentManager.getInstance(GradingConfiguration.class);
        }
        return result;
    }

    @Override
    public GradingManager getInstance(String hint) throws GradingException
    {
        try {
            GradingManager result;
            if (!this.contextComponentManager.hasComponent(GradingManager.class, hint)) {
                // step 2: retrieve the configuration
                GradingConfiguration gradingConfiguration = this.getGradingConfiguration(hint);

                // step 3: use the configuration information to retrieve the GradingManager and its descriptor.
                ComponentDescriptor<GradingManager> componentDescriptor = this.contextComponentManager
                    .getComponentDescriptor(GradingManager.class, gradingConfiguration.getStorageHint());
                result = this.contextComponentManager.getInstance(GradingManager.class,
                    gradingConfiguration.getStorageHint());

                // step 4: set the information of the GradingManager
                result.setGradingConfiguration(gradingConfiguration);
                result.setIdentifer(hint);

                // step 5: copy the descriptor and modifies the hint and the instantiation strategy
                DefaultComponentDescriptor<GradingManager> componentDescriptorCopy =
                    new DefaultComponentDescriptor<>(componentDescriptor);
                componentDescriptorCopy.setRoleHint(hint);
                componentDescriptorCopy.setInstantiationStrategy(ComponentInstantiationStrategy.SINGLETON);

                // step 6: register it in the current component manager for next request.
                this.currentComponentManager.registerComponent(componentDescriptorCopy, result);
            } else {
                // step 1, return directly the component if it can be found.
                result = this.contextComponentManager.getInstance(GradingManager.class, hint);
            }
            return result;
        } catch (ComponentLookupException | ComponentRepositoryException e) {
            throw new GradingException("Error when trying to get a GradingManager", e);
        }
    }
}
