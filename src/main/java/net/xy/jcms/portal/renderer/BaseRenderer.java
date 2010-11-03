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

public class BaseRenderer implements IBaseRenderer {

    @Override
    public StringBuilder renderStartTag(final String tag) {
        return new StringBuilder("<").append(tag).append(">");
    }

    @Override
    public StringBuilder renderStartTag(final String tag, final String style) {
        return new StringBuilder("<").append(tag).append(" class=\"").append(style).append("\" >");
    }

    @Override
    public StringBuilder renderStartTag(final String tag, final Map<String, String> attributes) {
        final StringBuilder ret = new StringBuilder("<").append(tag);
        if (attributes != null && !attributes.isEmpty()) {
            for (final Entry<String, String> attrib : attributes.entrySet()) {
                ret.append(" ").append(attrib.getKey()).append("=\"").append(attrib.getValue()).append("\"");
            }
        }
        return ret.append(" >");
    }

    @Override
    public StringBuilder renderEndTag(final String tag) {
        return new StringBuilder("</").append(tag).append(">");
    }

}
