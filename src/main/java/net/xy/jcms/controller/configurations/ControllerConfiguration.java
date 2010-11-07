/**
 *  This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 *
 *  XY.JCms is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XY.JCms is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XY.JCms.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.controller.configurations;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.configurations.stores.ClientStore;
import net.xy.jcms.controller.usecase.IController;

/**
 * configures the controller sectionwise and global
 * 
 * @author Xyan
 * 
 */
public class ControllerConfiguration extends Configuration<Map<String, Map<String, String>>> {
    /**
     * gloabl type constant for this type
     */
    public static final ConfigurationType TYPE = ConfigurationType.ControllerConfiguration;

    /**
     * default
     * 
     * @param configurationValue
     */
    public ControllerConfiguration(final Map<String, Map<String, String>> configurationValue) {
        super(TYPE, configurationValue);
    }

    @Override
    public void mergeConfiguration(final Configuration<Map<String, Map<String, String>>> otherConfig) {
        getConfigurationValue().putAll(otherConfig.getConfigurationValue());
    }

    /**
     * gets the globals
     * 
     * @return never null
     */
    public Map<String, String> getGlobals() {
        if (!getConfigurationValue().containsKey(GLOBAL_CONFIG)) {
            getConfigurationValue().put(GLOBAL_CONFIG, new HashMap<String, String>());
        }
        return getConfigurationValue().get(GLOBAL_CONFIG);
    }

    /**
     * gets an controller class associated config map
     * 
     * @param clazz
     * @return never null instead it creates an config for this controller
     */
    public Map<String, String> getControllerConfig(final Class<? extends IController> clazz) {
        if (!IController.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Obmitted class is no controller");
        }
        final String ctrlName = clazz.getSimpleName();
        if (!getConfigurationValue().containsKey(ctrlName)) {
            getConfigurationValue().put(ctrlName, new HashMap<String, String>());
        }
        return getConfigurationValue().get(ctrlName);
    }

    @Override
    public int hashCode() {
        return getConfigurationValue().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return getConfigurationValue().equals(object);
    }

    private ClientStore store = null;

    /**
     * returns an clientStore
     * 
     * @return value never null
     */
    public ClientStore getClientStore() {
        return store;
    }

    /**
     * sets an new client store
     * 
     * @param store
     */
    public void setClientStore(final ClientStore store) {
        this.store = store;
    }

    /**
     * global config identifier
     */
    public static final String GLOBAL_CONFIG = "GLOBAL";

    /**
     * parses an string to an controller goniguration each controller gets the
     * global and its all other configuration.
     * 
     * @param in
     * @return
     */
    public static Configuration<?> initByString(final String in) {
        final Map<String, Map<String, String>> config = new HashMap<String, Map<String, String>>();
        final String[] lines = in.split("\n");
        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i].trim();
            if (StringUtils.isBlank(line) || line.startsWith("#")) {
                continue;
            }
            if (line.matches("^[A-Z]{1}[a-zA-Z0-9]+\\{$")) {
                // start controller section
                final String controllerName = line.substring(0, line.length() - 1);
                if (!config.containsKey(controllerName)) {
                    config.put(controllerName, new HashMap<String, String>());
                }
                final Map<String, String> ctrlConfig = config.get(controllerName);
                // forward to next line until } reached
                i++;
                for (; i < lines.length; i++) {
                    final String ctrlLine = lines[i].trim();
                    if (ctrlLine.contentEquals("}")) {
                        break;
                    }
                    if (StringUtils.isBlank(ctrlLine) || ctrlLine.startsWith("#")) {
                        continue;
                    }
                    addLine(ctrlLine, ctrlConfig);
                }
            } else {
                // fill in globals section
                if (!config.containsKey(GLOBAL_CONFIG)) {
                    config.put(GLOBAL_CONFIG, new HashMap<String, String>());
                }
                final Map<String, String> global = config.get(GLOBAL_CONFIG);
                addLine(line, global);
            }
        }
        return new ControllerConfiguration(config);
    }

    /**
     * helper function which proccesses multiple line patterns
     * 
     * @param line
     * @param map
     */
    private static void addLine(final String line, final Map<String, String> map) {
        if (line.contains("=")) {
            // asignment pattern
            final String[] values = line.split("=", 2);
            map.put(values[0].trim(), values[1].trim());
        } else {
            // flag pattern
            map.put(line.trim(), "true");
        }
    }
}
