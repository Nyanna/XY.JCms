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

/**
 * renders all links as pseudo buttons
 * 
 * @author Xyan
 * 
 */
public class ButtonLinkRenderer extends BaseRenderer {

    @Override
    public StringBuilder renderLinkStart(final String href, final String title, final String rel) {
        // TODO [LOW] remove fmd styleclass
        return new StringBuilder("<span class=\"fmd-button\"><span>").append(super.renderLinkStart(href, title, rel));
    }

    @Override
    public StringBuilder renderLinkEnd() {
        return super.renderLinkEnd().append("</span></span>");
    }
}
