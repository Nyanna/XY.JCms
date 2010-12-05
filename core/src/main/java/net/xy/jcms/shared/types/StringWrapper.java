/**
 * This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.JCms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * XY.JCms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with XY.JCms. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.shared.types;

import net.xy.jcms.shared.IConverter;

/**
 * simple wrapps/returns an string
 * 
 * @author xyan
 * 
 */
public class StringWrapper implements IConverter {

    @Override
    public void fromString(final String str) {
        // nothing to convert
        return;
    }

    @Override
    public Object convert(final String str) {
        return str;
    }

}
