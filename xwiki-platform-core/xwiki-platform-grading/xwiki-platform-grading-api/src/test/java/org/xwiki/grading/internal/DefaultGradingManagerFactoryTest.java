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

import org.junit.jupiter.api.Test;
import org.xwiki.component.descriptor.ComponentDescriptor;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.grading.GradingConfiguration;
import org.xwiki.grading.GradingManager;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DefaultGradingManagerFactory}.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@ComponentTest
public class DefaultGradingManagerFactoryTest
{
    @InjectMockComponents
    private DefaultGradingManagerFactory factory;

    @MockComponent
    @Named("context")
    private ComponentManager contextComponentManager;

    @MockComponent
    private ComponentManager currentComponentManager;

    @MockComponent
    private GradingManager gradingManager;

    @MockComponent
    private GradingConfiguration gradingConfiguration;

    @Test
    void getExistingInstance() throws Exception
    {
        String hint = "existingInstance";
        when(this.contextComponentManager.hasComponent(GradingManager.class, hint)).thenReturn(true);
        when(this.contextComponentManager.getInstance(GradingManager.class, hint)).thenReturn(gradingManager);

        assertSame(gradingManager, this.factory.getInstance(hint));
        verify(this.currentComponentManager, never()).registerComponent(any(), any());
    }

    @Test
    void getNewInstanceCustomConfiguration() throws Exception
    {
        String hint = "newInstance";
        when(this.contextComponentManager.hasComponent(GradingManager.class, hint)).thenReturn(false);
        when(this.contextComponentManager.hasComponent(GradingConfiguration.class, hint)).thenReturn(true);
        when(this.contextComponentManager.getInstance(GradingConfiguration.class, hint))
            .thenReturn(this.gradingConfiguration);
        when(this.gradingConfiguration.getStorageHint()).thenReturn("someStorage");
        when(this.contextComponentManager.getInstance(GradingManager.class, "someStorage"))
            .thenReturn(this.gradingManager);
        ComponentDescriptor componentDescriptor = mock(ComponentDescriptor.class);
        when(componentDescriptor.getImplementation()).thenReturn(DefaultGradingManager.class);
        when(this.contextComponentManager.getComponentDescriptor(GradingManager.class, "someStorage"))
            .thenReturn(componentDescriptor);
        DefaultComponentDescriptor<GradingManager> expectedComponentDescriptor = new DefaultComponentDescriptor<>();
        expectedComponentDescriptor.setImplementation(DefaultGradingManager.class);
        expectedComponentDescriptor.setRoleHint(hint);
        expectedComponentDescriptor.setInstantiationStrategy(ComponentInstantiationStrategy.SINGLETON);

        assertSame(this.gradingManager, this.factory.getInstance(hint));
        verify(this.currentComponentManager).registerComponent(expectedComponentDescriptor, this.gradingManager);
    }

    @Test
    void getNewInstanceDefaultConfiguration() throws Exception
    {
        String hint = "newInstance";
        when(this.contextComponentManager.hasComponent(GradingManager.class, hint)).thenReturn(false);
        when(this.contextComponentManager.hasComponent(GradingConfiguration.class, hint)).thenReturn(false);
        when(this.contextComponentManager.getInstance(GradingConfiguration.class))
            .thenReturn(this.gradingConfiguration);
        when(this.gradingConfiguration.getStorageHint()).thenReturn("someStorage");
        when(this.contextComponentManager.getInstance(GradingManager.class, "someStorage"))
            .thenReturn(this.gradingManager);
        ComponentDescriptor componentDescriptor = mock(ComponentDescriptor.class);
        when(componentDescriptor.getImplementation()).thenReturn(DefaultGradingManager.class);
        when(this.contextComponentManager.getComponentDescriptor(GradingManager.class, "someStorage"))
            .thenReturn(componentDescriptor);
        DefaultComponentDescriptor<GradingManager> expectedComponentDescriptor = new DefaultComponentDescriptor<>();
        expectedComponentDescriptor.setImplementation(DefaultGradingManager.class);
        expectedComponentDescriptor.setRoleHint(hint);
        expectedComponentDescriptor.setInstantiationStrategy(ComponentInstantiationStrategy.SINGLETON);

        assertSame(this.gradingManager, this.factory.getInstance(hint));
        verify(this.currentComponentManager).registerComponent(expectedComponentDescriptor, this.gradingManager);
    }
}
