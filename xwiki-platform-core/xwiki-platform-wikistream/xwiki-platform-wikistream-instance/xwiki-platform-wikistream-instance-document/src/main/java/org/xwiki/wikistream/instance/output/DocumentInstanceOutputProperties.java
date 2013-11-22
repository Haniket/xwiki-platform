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
package org.xwiki.wikistream.instance.output;

import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.properties.annotation.PropertyDescription;
import org.xwiki.properties.annotation.PropertyHidden;
import org.xwiki.properties.annotation.PropertyName;
import org.xwiki.rendering.syntax.Syntax;

/**
 * @version $Id$
 * @since 5.3RC1
 */
public class DocumentInstanceOutputProperties extends InstanceOutputProperties
{
    /**
     * @see #getDefaultReference()
     */
    private EntityReference defaultReference;

    /**
     * @see #getSaveComment()
     */
    private String saveComment = "Import";

    /**
     * @see #getAuthor()
     */
    private DocumentReference author;

    private boolean authorSet;

    /**
     * @see #getDefaultSyntax()
     */
    private Syntax defaultSyntax;

    /**
     * @see #isPreviousDeleted()
     */
    private boolean previousDeleted;

    /**
     * @see #isVersionPreserved()
     */
    private boolean versionPreserved;

    /**
     * @see #isPreserveAuthor()
     */
    private boolean authorPreserved;

    /**
     * @return The base reference to use to resolve reference from events
     */
    @PropertyName("Default reference")
    @PropertyDescription("The base reference to use to resolve reference from events")
    public EntityReference getDefaultReference()
    {
        return this.defaultReference;
    }

    /**
     * @param defaultReference The base reference to use to resolve reference from events
     */
    public void setDefaultReference(EntityReference defaultReference)
    {
        this.defaultReference = defaultReference;
    }

    /**
     * @return The comment to set when saving a document
     */
    @PropertyName("Save comment")
    @PropertyDescription("The comment to set when saving a document")
    public String getSaveComment()
    {
        return this.saveComment;
    }

    /**
     * @param saveComment The comment to set when saving a document
     */
    public void setSaveComment(String saveComment)
    {
        this.saveComment = saveComment;
    }

    /**
     * @return The author to set when saving a document
     */
    @PropertyName("Author")
    @PropertyDescription("The author to set when saving a document")
    public DocumentReference getAuthor()
    {
        return this.author;
    }

    /**
     * @param author The author to set when saving a document
     */
    public void setAuthor(DocumentReference author)
    {
        this.author = author;
        this.authorSet = true;
    }

    /**
     * @return indicate of the author has been set
     */
    @PropertyHidden
    public boolean isAuthorSet()
    {
        return this.authorSet;
    }

    /**
     * @return The default syntax if not is provided in events
     */
    @PropertyName("Default syntax")
    @PropertyDescription("The default syntax if not is provided in events")
    public Syntax getDefaultSyntax()
    {
        return this.defaultSyntax;
    }

    /**
     * @param defaultSyntax The default syntax if not is provided in events
     */
    public void setDefaultSyntax(Syntax defaultSyntax)
    {
        this.defaultSyntax = defaultSyntax;
    }

    /**
     * @return Indicate of existing document should be deleted before importing the new one
     */
    @PropertyName("Delete existing document")
    @PropertyDescription("Indicate of existing document should be deleted before importing the new one")
    public boolean isPreviousDeleted()
    {
        return this.previousDeleted;
    }

    /**
     * @param previousDeleted Indicate of existing document should be deleted before importing the new one
     */
    public void setPreviousDeleted(boolean previousDeleted)
    {
        this.previousDeleted = previousDeleted;
    }

    /**
     * @return Indicate of the versions coming from the events should be kept
     */
    @PropertyName("Preserve version")
    @PropertyDescription("Indicate if the versions coming from the events should be kept")
    public boolean isVersionPreserved()
    {
        return this.versionPreserved;
    }

    /**
     * @param versionPreserved Indicate of the versions coming from the events should be kept
     */
    public void setVersionPreserved(boolean versionPreserved)
    {
        this.versionPreserved = versionPreserved;
    }

    /**
     * @return Indicate of the authors comming from the events should be kept. Not taken into account if
     *         {@link #setAuthor(DocumentReference)} is used.
     */
    @PropertyName("Preserve author")
    @PropertyDescription(
        "Indicate of the authors comming from the events should be kept. Not taken into account if AUTHOR is set")
    public boolean isAuthorPreserved()
    {
        return authorPreserved;
    }

    /**
     * @param authorPreserved Indicate of the authors comming from the events should be kept. Not taken into account if
     *            {@link #setAuthor(DocumentReference)} is used.
     */
    public void setAuthorPreserved(boolean authorPreserved)
    {
        this.authorPreserved = authorPreserved;
    }
}
