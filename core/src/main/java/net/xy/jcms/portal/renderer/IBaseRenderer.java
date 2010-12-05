/**
 * This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.JCms is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * XY.JCms is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with XY.JCms. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.portal.renderer;

import java.util.Map;

import net.xy.jcms.shared.IRenderer;

/**
 * baserenderer interface for xhtml
 * 
 * @author Xyan
 * 
 */
public interface IBaseRenderer extends IRenderer {

    /**
     * renders an xml starttag without attributes
     * 
     * @return value
     */
    public StringBuilder renderStartTag(final String tag);

    /**
     * renders an xml starttag with an style attribute
     * 
     * @param tag
     * @param style
     * @return value
     */
    public StringBuilder renderStartTag(final String tag, final String style);

    /**
     * renders an xml starttag with an variable attribute list
     * 
     * @param tag
     * @param attributes
     * @return value
     */
    public StringBuilder renderStartTag(final String tag, final Map<String, String> attributes);

    /**
     * renders an xml endtag
     * 
     * @return value
     */
    public StringBuilder renderEndTag(final String tag);

    /**
     * method for rendering meta link tags in html
     * 
     * @param href
     * @return value
     */
    public StringBuilder renderMetaLink(final String href);

    /**
     * renders various script inclusions
     * 
     * @param href
     * @return value
     */
    public StringBuilder renderScriptInclude(final String scriptUri);

    /**
     * render an link startTag
     * 
     * @param href
     *            link reference
     * @param title
     *            the title of the link
     * @param rel
     *            seo/crawler param
     * @return value
     */
    public StringBuilder renderLinkStart(final String href, final String title, final String rel);

    /**
     * render an closing link tag
     * 
     * @return value
     */
    public StringBuilder renderLinkEnd();

    /**
     * renders an standalone im image
     * 
     * @param src
     *            image
     * @param alt
     *            if null title would be asumed
     * @param title
     *            if null alt would be asumed
     * @return value
     */
    public StringBuilder renderImage(final String src, final String alt, final String title);
}
