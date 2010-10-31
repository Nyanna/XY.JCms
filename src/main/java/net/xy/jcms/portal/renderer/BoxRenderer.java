/**
 *  This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 *
 *  XY.JCms is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XY.JCms is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XY.JCms.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.portal.renderer;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * standard boxrenderer rendering divs
 * 
 * @author Xyan
 * 
 */
public class BoxRenderer extends BaseRenderer implements IBoxRenderer {

    @Override
    public StringBuilder renderBegin(final String styleClass) {
        if (StringUtils.isNotBlank(styleClass)) {
            return new StringBuilder("<div class=" + StringEscapeUtils.escapeHtml(styleClass) + ">");
        }
        return new StringBuilder("<div>");
    }

    @Override
    public StringBuilder renderEnd() {
        return new StringBuilder("</div>");
    }

}
