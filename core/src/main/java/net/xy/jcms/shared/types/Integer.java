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
 * simply wrapps and returns an Integer
 * 
 * @author xyan
 * 
 */
public class Integer implements IConverter<java.lang.Integer> {

    @Override
    public void fromString(final String str) {
        return;
    }

    @Override
    public java.lang.Integer convert(final String str) {
        return convertString(str);
    }

    /**
     * static access to method
     * 
     * @param str
     * @return converted value
     */
    public static java.lang.Integer convertString(final String str) {
        return java.lang.Integer.valueOf(str);
    }

    @Override
    public String convert(final Object obj) {
        return obj.toString();
    }

    @Override
    public java.lang.Integer valueOf(final Object obj) {
        return valueOfObject(obj);
    }

    /**
     * static access to method
     * 
     * @param obj
     * @return converted value
     */
    public static java.lang.Integer valueOfObject(final Object obj) {
        if (obj instanceof String) {
            return convertString((String) obj);
        } else if (obj instanceof java.lang.Integer) {
            return (java.lang.Integer) obj;
        } else {
            return null;
        }
    }
}
