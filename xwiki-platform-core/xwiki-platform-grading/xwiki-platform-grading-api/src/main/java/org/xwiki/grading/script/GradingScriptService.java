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
package org.xwiki.grading.script;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.grading.Grading;
import org.xwiki.grading.GradingConfiguration;
import org.xwiki.grading.GradingException;
import org.xwiki.grading.GradingManager;
import org.xwiki.grading.GradingManagerFactory;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;
import org.xwiki.user.UserReference;
import org.xwiki.user.UserReferenceResolver;

import com.xpn.xwiki.XWikiContext;

/**
 * Script service to manipulate rankings.
 *
 * @version $Id$
 * @since 12.8RC1
 */
@Component
@Singleton
@Named("grading")
@Unstable
public class GradingScriptService implements ScriptService
{
    @Inject
    private GradingManagerFactory gradingManagerFactory;

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    @Named("document")
    private UserReferenceResolver<DocumentReference> userReferenceResolver;

    @Inject
    private Logger logger;

    private UserReference getCurrentUserReference()
    {
        return this.userReferenceResolver.resolve(this.contextProvider.get().getUserReference());
    }

    /**
     * Allows to save a grading for the given reference, with the current user reference,
     * by using the given manager hint.
     *
     * @param managerHint the hint of the manager to use for saving this grade. (see {@link GradingManagerFactory}).
     * @param reference the reference for which to save a grade.
     * @param grade the grade to save.
     * @return an optional containing the {@link Grading} value, or empty in case of problem or if the grade is 0 and
     *      the configuration doesn't allow to save 0 values (see {@link GradingConfiguration#storeZero()}).
     */
    public Optional<Grading> saveGrading(String managerHint, EntityReference reference, int grade)
    {
        try {
            GradingManager gradingManager = this.gradingManagerFactory.getInstance(managerHint);
            Grading grading = gradingManager.saveGrading(reference, this.getCurrentUserReference(), grade);
            if (grading != null) {
                return Optional.of(grading);
            }
        } catch (GradingException e) {
            logger.error("Error while trying to grade reference [{}].", reference, ExceptionUtils.getRootCause(e));
        }
        return Optional.empty();
    }

    /**
     * Retrieve gradings information for the given reference on the given manager.
     *
     * @param managerHint the hint of the manager to use for retrieving grading information.
     *                  (see {@link GradingManagerFactory}).
     * @param reference the reference for which to retrieve grading information.
     * @param offset the offset at which to start for retrieving information.
     * @param limit the limit number of information to retrieve.
     * @return a list of gradings containing a maximum of {@code limit} values.
     */
    public List<Grading> getGradings(String managerHint, EntityReference reference, int offset, int limit)
    {
        try {
            GradingManager gradingManager = this.gradingManagerFactory.getInstance(managerHint);
            Map<GradingManager.GradingQueryField, Object> queryParameters = new HashMap<>();
            queryParameters.put(GradingManager.GradingQueryField.ENTITY_REFERENCE, reference);
            queryParameters.put(GradingManager.GradingQueryField.ENTITY_TYPE, reference.getType());
            return gradingManager.getGradings(queryParameters, offset, limit,
                GradingManager.GradingQueryField.UPDATED_DATE, false);
        } catch (GradingException e) {
            logger.error("Error when getting gradings for reference [{}].", reference, ExceptionUtils.getRootCause(e));
            return Collections.emptyList();
        }
    }
}
