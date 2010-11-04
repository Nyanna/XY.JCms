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
package net.xy.jcms.controller.configurations;

import java.util.ArrayList;
import java.util.List;

import net.xy.jcms.shared.IRenderer;

/**
 * Configuration dummy which only collects the configuration informations
 * 
 * @author xyan
 * 
 */
public class RenderKitConfigurationDummy extends RenderKitConfiguration {
    /**
     * stores requested interface names
     */
    private final List<String> ifaces = new ArrayList<String>();

    /**
     * default
     */
    public RenderKitConfigurationDummy() {
        super(null);
    }

    @Override
    public IRenderer get(final Class<? extends IRenderer> rInterface, final ComponentConfiguration config) {
        ifaces.add(ConfigurationIterationStrategy.fullPath(config, rInterface.getSimpleName()));
        ifaces.add(rInterface.getSimpleName());
        return new IRenderer() {};
    }

    /**
     * get the collected interface names
     * 
     * @return
     */
    public List<String> getInterfaceNames() {
        return ifaces;
    }

}
