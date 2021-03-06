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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.xy.jcms.shared.IRenderer;

/**
 * Configuration dummy which only collects the configuration informations
 * 
 * @author xyan
 * 
 */
public class RenderKitConfigurationProxy extends RenderKitConfiguration {
    /**
     * stores present requested interface names
     */
    private final Map<String, String> presentIfaces = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * stores missing requested interface names
     */
    private final List<String> missingIfaces = new ArrayList<String>();

    /**
     * default empty proxy
     */
    public RenderKitConfigurationProxy() {
        super(new HashMap<String, IRenderer>());
    }

    /**
     * proxy passing the specified config
     * 
     * @param config
     */
    public RenderKitConfigurationProxy(final RenderKitConfiguration config) {
        super(config.getConfigurationValue());
    }

    @Override
    public IRenderer get(final Class<? extends IRenderer> rInterface, final ComponentConfiguration config) {
        Match<String, IRenderer> value = new Match<String, IRenderer>(null, null);
        try {
            value = super.getMatch(rInterface, config);
        } catch (final IllegalArgumentException ex) {
        }
        if (value.getValue() != null) {
            presentIfaces.put(value.getPath(), value.getValue().getClass()
                    .getName());
            return value.getValue();
        } else {
            final String full = ConfigurationIterationStrategy.fullPath(config, rInterface.getSimpleName());
            if (!missingIfaces.contains(full)) {
                missingIfaces.add(full);
            }
            final String simple = rInterface.getSimpleName();
            if (!missingIfaces.contains(simple)) {
                missingIfaces.add(simple);
            }
            return new IRenderer() {
            };
        }
    }

    /**
     * get the collected interface names
     * 
     * @return value
     */
    public Map<String, String> getInterfaceNames() {
        final Map<String, String> merge = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        merge.putAll(presentIfaces);
        for (final String key : missingIfaces) {
            merge.put(key, "");
        }
        return merge;
    }

    /**
     * returns already configured renderers
     * 
     * @return value
     */
    public Map<String, String> getPresentInterfaceNames() {
        return presentIfaces;
    }

    /**
     * returns only missing configurations
     * 
     * @return value
     */
    public List<String> getMissingInterfaceNames() {
        Collections.sort(missingIfaces, String.CASE_INSENSITIVE_ORDER);
        return missingIfaces;
    }

    /**
     * returns true if a config is missing
     * 
     * @return value
     */
    public boolean isMissing() {
        return missingIfaces.isEmpty() ? false : true;
    }

}
