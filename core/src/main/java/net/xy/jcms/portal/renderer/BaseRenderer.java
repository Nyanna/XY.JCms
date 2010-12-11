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
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

/**
 * common base renderer for xhtml
 * 
 * @author xyan
 * 
 */
public class BaseRenderer implements IBaseRenderer {

    /**
     * renders an xml starttag without attributes
     * 
     * @return value
     */
    protected StringBuilder renderStartTag(final String tag) {
        return renderStartTag(tag, null, null);
    }

    /**
     * renders an xml starttag with an style attribute
     * 
     * @param tag
     * @param style
     * @return value
     */
    protected StringBuilder renderStartTag(final String tag, final String style) {
        return renderStartTag(tag, style, null);
    }

    /**
     * 
     * @param tag
     * @param style
     * @param id
     * @return
     */
    protected StringBuilder renderStartTag(final String tag, final String style, final String id) {
        final StringBuilder ret = new StringBuilder("<").append(tag);
        if (StringUtils.isNotBlank(style)) {
            ret.append(" class=\"").append(style.trim()).append("\"");
        }
        if (StringUtils.isNotBlank(id)) {
            ret.append(" id=\"").append(id.trim()).append("\"");
        }
        return ret.append(" >");
    }

    /**
     * renders an xml starttag with an variable attribute list
     * 
     * @param tag
     * @param attributes
     * @return value
     */
    protected StringBuilder renderStartTag(final String tag, final Map<String, String> attributes) {
        final StringBuilder ret = new StringBuilder("<").append(tag);
        if (attributes != null && !attributes.isEmpty()) {
            for (final Entry<String, String> attrib : attributes.entrySet()) {
                ret.append(" ").append(attrib.getKey()).append("=\"").append(attrib.getValue()).append("\"");
            }
        }
        return ret.append(" >");
    }

    /**
     * renders an xml endtag
     * 
     * @return value
     */
    protected StringBuilder renderEndTag(final String tag) {
        return new StringBuilder("</").append(tag).append(">");
    }

    @Override
    public StringBuilder renderMetaLink(final String href) {
        return new StringBuilder("<link href=\"").append(href).append(
                "\" media=\"all\" type=\"text/css\" rel=\"stylesheet\">");
    }

    @Override
    public StringBuilder renderScriptInclude(final String scriptUri) {
        return new StringBuilder("<script src=\"").append(scriptUri).append("\" type=\"text/javascript\"></script>");
    }

    @Override
    public StringBuilder renderLinkStart(final String href, final String title, final String rel) {
        final StringBuilder ret = new StringBuilder("<a").append(" href=\"").append(href).append("\"");
        if (title != null) {
            ret.append(" title=\"").append(title).append("\"");
        }
        if (rel != null) {
            ret.append(" rel=\"").append(title).append("\"");
        }
        return ret.append("/>");
    }

    @Override
    public StringBuilder renderLinkEnd() {
        return renderEndTag("a");
    }

    @Override
    public StringBuilder renderImage(final String src, final String alt, final String title) {
        final String ialt = alt != null ? alt : title;
        final String ititle = title != null ? title : alt;
        final StringBuilder ret = new StringBuilder("<img src=\"").append(src).append("\"");
        if (ialt != null) {
            ret.append(" alt=\"").append(ialt).append("\"");
        }
        if (ititle != null) {
            ret.append(" title=\"").append(title).append("\"");
        }
        return ret.append("/>");
    }

    @Override
    public StringBuilder renderJavaScriptStart() {
        return new StringBuilder("<script type=\"text/javascript\">\r\n" +
                "// <![CDATA[\r\n");
    }

    @Override
    public StringBuilder renderJavaScriptEnd() {
        return new StringBuilder("\r\n// ]]>\r\n" +
                "</script>");
    }

    @Override
    public StringBuilder renderHeading(final int lvl, final String containment) {
        return new StringBuilder(renderStartTag("h" + lvl)).append(containment).append(renderEndTag("h" + lvl));
    }

    @Override
    public StringBuilder renderPageStart(final Map<String, String> attributes) {
        return renderStartTag("body", attributes);
    }

    @Override
    public StringBuilder renderPageEnd() {
        return renderEndTag("body");
    }

}
