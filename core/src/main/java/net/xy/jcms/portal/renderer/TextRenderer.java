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

/**
 * an simple text renderer, with the abbility to render html containers.
 * 
 * @author Xyan
 * 
 */
public class TextRenderer extends BaseRenderer implements ITextRenderer {

    @Override
    public StringBuilder renderText(final String text) {
        return new StringBuilder(text);
    }

    @Override
    public StringBuilder renderTextInContainer(final String text, final String container) {
        return new StringBuilder(renderStartTag(container)).append(text).append(renderEndTag(container));
    }

    @Override
    public StringBuilder renderTextWithStyle(final String text, String container, final String style) {
        if (container == null) {
            container = "em";
        }
        return new StringBuilder(renderStartTag(container, style)).append(text).append(renderEndTag(container));
    }

}
