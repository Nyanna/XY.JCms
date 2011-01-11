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

import java.util.Map;

/**
 * type converters which need to be initialized to be usefull. Actually only implemented for the translation
 * configuration.
 * 
 * @author xyan
 * 
 * @param <T>
 *            result type to get to converted
 */
public interface InitializableController<T> extends IConverter<T> {

    /**
     * initializes the converter by these options
     * 
     * @param options
     * @return an initialized converter
     */
    public IConverter<T> initialize(Map<String, String> options);

    /**
     * gets the initialization parameters back so converter should store them internally
     * 
     * @return parameter map
     */
    public Map<String, String> store();
}
