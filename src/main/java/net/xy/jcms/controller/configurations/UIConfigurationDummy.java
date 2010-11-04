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

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration dummy which only collects the configuration informations
 * 
 * @author xyan
 * 
 */
public class UIConfigurationDummy extends UIConfiguration {

    /**
     * stores the requested ui config
     */
    private final Map<String, UI<?>> prepared = new HashMap<String, UI<?>>();

    /**
     * default
     */
    public UIConfigurationDummy() {
        super(null);
    }

    @Override
    public Object getConfig(final UI<?> ui, final ComponentConfiguration config) {
        prepared.put(ConfigurationIterationStrategy.fullPath(config, ui.getKey()), ui);
        return new Object();
    }

    /**
     * returns all at collectec ui configs
     * 
     * @return
     */
    public Map<String, UI<?>> getPrepared() {
        return prepared;
    }

}
