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
package net.xy.jcms.shared;

/**
 * an interface for loose coupled type conversion from/to property strings. can
 * be used for human readable configuration
 * mainly via xml.
 * Converter need the possibility to get instantiated by an parameterless
 * constructor.
 * 
 * @author Xyan
 * 
 */
public interface IConverter<T> {

    /**
     * must implement an method accepting an string which converts int this
     * class type
     * 
     * @param str
     */
    public void fromString(final String str);

    /**
     * can return an object from an given string
     * 
     * @return value
     */
    public T convert(final String str);

    /**
     * method converts the object back to an value
     * 
     * @param obj
     * @return string representation able to get parsed by this converter
     */
    public String convert(final Object obj);

    /**
     * tries to convert any object type to this object type
     * 
     * @param obj
     * @return object of this type
     */
    public T valueOf(final Object obj);
}
