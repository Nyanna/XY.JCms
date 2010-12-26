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

import java.util.TreeMap;

import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;

/**
 * an simply typesation for an configuration container object
 * 
 * @author Xyan
 * 
 */
public class Model extends TreeMap<ConfigurationType, Configuration<?>> {

    private static final long serialVersionUID = 1335232176002098094L;

    /**
     * default
     */
    public Model() {
    }

    /**
     * simple unwraps to an map
     * 
     * @param model
     */
    public Model(final Model model) {
        super(model);
    }

}
