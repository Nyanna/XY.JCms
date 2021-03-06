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
package net.xy.jcms.shared;

import net.xy.jcms.controller.configurations.ComponentConfiguration;

/**
 * general component interface. an component is the main element in the tree. also fragments are components with
 * degraded features.
 * 
 * @author xyan
 * 
 */
public interface IComponent {

    /**
     * returns the components configuration preparing alghorhythms
     * 
     * @return an configuration which describes all requested configs
     */
    public ComponentConfiguration getConfiguration();

    /**
     * starts the rendering run
     * 
     * @param out
     *            the outwritter adaption
     * @param config
     */
    public void render(final IOutWriter out, final ComponentConfiguration config);
}
