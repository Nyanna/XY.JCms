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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
public class ControllerConfiguration extends Configuration<Map<String, Map<String, Object>>> {
    /**
     * gloabl type constant for this type
     */
    public static final ConfigurationType TYPE = ConfigurationType.ControllerConfiguration;

    /**
     * default
     * 
     * @param configurationValue
     */
    public ControllerConfiguration(final Map<String, Map<String, Object>> configurationValue) {
        super(TYPE, configurationValue);
    }

    @Override
    public void mergeConfiguration(final Configuration<Map<String, Map<String, Object>>> otherConfig) {
        getConfigurationValue().putAll(otherConfig.getConfigurationValue());
        // no recursive merging new controller config overwrites old one
    }

    /**
     * gets the globals
     * 
     * @return never null
     */
    public Map<String, Object> getGlobals() {
        if (!getConfigurationValue().containsKey(GLOBAL_CONFIG)) {
            getConfigurationValue().put(GLOBAL_CONFIG, new HashMap<String, Object>());
        }
        return getConfigurationValue().get(GLOBAL_CONFIG);
    }

    /**
     * gets an controller class associated config map
     * 
     * @param clazz
     * @return never null instead it creates an config for this controller
     */
    public Map<String, Object> getControllerConfig(final Class<? extends IController> clazz) {
        if (!IController.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Obmitted class is no controller");
        }
        final String ctrlName = clazz.getSimpleName();
        if (!getConfigurationValue().containsKey(ctrlName)) {
            getConfigurationValue().put(ctrlName, new HashMap<String, Object>());
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
     * @return value
     */
    @SuppressWarnings("unchecked")
    public static Configuration<?> initByString(final String in) {
        final Map<String, Map<String, Object>> config = new HashMap<String, Map<String, Object>>();
        final String[] lines = in.split("\n");
        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i].trim();
            if (StringUtils.isBlank(line) || line.startsWith("#")) {
                continue;
            }
            if (line.matches("^[A-Z]{1}[a-zA-Z0-9]+\\{$")) {
                // start controller section
                final String controllerName = line.substring(0, line.length() - 1);
                // forward to next line until } reached
                i++;
                if (!config.containsKey(controllerName)) {
                    config.put(controllerName, new LinkedHashMap<String, Object>());
                }
                final Map<String, Object> ctrlConfig = config.get(controllerName);
                for (; i < lines.length; i++) {
                    final String ctrlLine = lines[i].trim();
                    if (ctrlLine.contentEquals("}")) {
                        break;
                    }
                    if (StringUtils.isBlank(ctrlLine) || ctrlLine.startsWith("#")) {
                        continue;
                    }

                    if (ctrlLine.matches("^[a-zA-Z0-9.]+\\{$")) {
                        // begin of an controller config clustering
                        final String subSectionName = ctrlLine.substring(0, ctrlLine.length() - 1);
                        // forward to next line until } reached
                        i++;
                        final Object oldValue = ctrlConfig.get(subSectionName);
                        if (!List.class.isInstance(oldValue)) {
                            ctrlConfig.put(subSectionName, new LinkedList<Map<String, Object>>());
                            if (oldValue != null) {
                                final HashMap<String, Object> old = new HashMap<String, Object>();
                                old.put(subSectionName, oldValue);
                                ((List<Map<String, Object>>) ctrlConfig.get(subSectionName)).add(old);
                            }
                        }
                        final List<Map<String, Object>> subSections = (List<Map<String, Object>>) ctrlConfig
                                .get(subSectionName);
                        final Map<String, Object> subParam = new LinkedHashMap<String, Object>();
                        for (; i < lines.length; i++) {
                            final String subSec = lines[i].trim();
                            if (subSec.contentEquals("}")) {
                                break;
                            }
                            if (StringUtils.isBlank(subSec) || subSec.startsWith("#")) {
                                continue;
                            }
                            addLine(subSec, subParam);
                        }
                        if (!subParam.isEmpty()) {
                            subSections.add(subParam);
                        }
                    } else if (ctrlLine.contentEquals("}")) {
                        throw new IllegalArgumentException(
                                "There are unmatching controller subsection parentheses at line " + i + ".");
                    } else {
                        // simple controller config pair key = val
                        addLine(ctrlLine, ctrlConfig);
                    }
                }
            } else if (line.contentEquals("}")) {
                throw new IllegalArgumentException("There are global unmatching subsection parentheses at line " + i
                        + ".");
            } else {
                // fill in globals section
                if (!config.containsKey(GLOBAL_CONFIG)) {
                    config.put(GLOBAL_CONFIG, new HashMap<String, Object>());
                }
                final Map<String, Object> global = config.get(GLOBAL_CONFIG);
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
    private static void addLine(final String line, final Map<String, Object> map) {
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
