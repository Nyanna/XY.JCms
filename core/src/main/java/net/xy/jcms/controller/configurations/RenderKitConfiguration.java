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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.ClimbUp;
import net.xy.jcms.controller.configurations.pool.RendererPool;
import net.xy.jcms.persistence.MapEntry;
import net.xy.jcms.persistence.usecase.ConfigurationDTO;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IRenderer;

/**
 * is like the baserenderfactory an configuration delivering renderer instances
 * described by an interface
 * 
 * @author Xyan
 * 
 */
public class RenderKitConfiguration extends Configuration<Map<String, IRenderer>> {
    /**
     * gloabl type constant for this type
     */
    public static final ConfigurationType TYPE = ConfigurationType.RenderKitConfiguration;

    /**
     * default constructor by Class = IRenderer
     * 
     * @param configurationValue
     */
    public RenderKitConfiguration(final Map<String, IRenderer> configurationValue) {
        super(TYPE, configurationValue);
    }

    /**
     * return the singleton renderer instance
     * -full component path comp1.comp2.comp3.key
     * -parents comp1.comp2.key, comp1.key
     * 
     * @param rInterface
     * @return value
     */
    public IRenderer get(final Class<? extends IRenderer> rInterface, final ComponentConfiguration config) {
        return getMatch(rInterface, config).getValue();
    }

    /**
     * stores found config requests to save iteration strategies
     */
    private final Map<String, Match<String, IRenderer>> cache = enableCache ? new HashMap<String, Match<String, IRenderer>>()
            : null;

    /**
     * return the singleton renderer instance and where it was found, closure.
     * -full component path comp1.comp2.comp3.key
     * -parents comp1.comp2.key, comp1.key
     * 
     * @param rInterface
     * @param config
     * @return value
     */
    public Match<String, IRenderer> getMatch(final Class<? extends IRenderer> rInterface,
            final ComponentConfiguration config) {
        final String key = rInterface.getSimpleName();
        final String fullPathKey = ConfigurationIterationStrategy.fullPath(config, key);
        if (enableCache) {
            final Match<String, IRenderer> cached = cache.get(fullPathKey);
            if (cached != null) {
                return cached;
            }
        }
        Match<String, IRenderer> value = new Match<String, IRenderer>(null, null);
        final ClimbUp strategy = new ClimbUp(config, key);
        final List<String> retrievalStack = new ArrayList<String>();
        for (final String pathKey : strategy) {
            retrievalStack.add(pathKey);
            final IRenderer found = getConfigurationValue().get(pathKey);
            if (found != null) {
                value = new Match<String, IRenderer>(pathKey, found);
                break;
            }
        }
        if (value.getValue() != null) {
            if (enableCache) {
                cache.put(fullPathKey, value);
            }
            return value;
        } else {
            throw new IllegalArgumentException("An mendatory renderer was missing! "
                    + DebugUtils.printFields(key, retrievalStack));
        }
    }

    @Override
    public RenderKitConfiguration mergeConfiguration(final Configuration<Map<String, IRenderer>> otherConfig) {
        return mergeConfiguration(otherConfig.getConfigurationValue());
    }

    @Override
    public RenderKitConfiguration mergeConfiguration(final Map<String, IRenderer> otherConfig) {
        final Map<String, IRenderer> result = new HashMap<String, IRenderer>(getConfigurationValue());
        result.putAll(otherConfig);
        return new RenderKitConfiguration(result);
    }

    @Override
    public int hashCode() {
        return getConfigurationValue().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return getConfigurationValue().equals(object);
    }

    /**
     * creates an config based on parsing an string
     * 
     * @param configString
     * @param mounted
     *            where this configuration was inserted e.g. root.fragmentOne adjusts relative pathes of the form
     *            .comp4.comp5 with the given mountpoint to comp1.comp4.comp5
     * @return value
     */
    public static RenderKitConfiguration initByString(final String configString, final ClassLoader loader,
            final String mount) {
        final Map<String, IRenderer> result = new HashMap<String, IRenderer>();
        final String[] lines = configString.split("\n");
        for (final String line : lines) {
            if (StringUtils.isBlank(line) || line.trim().startsWith("#")) {
                continue;
            }
            final String[] parsed = line.trim().split("=", 2);
            try {
                String iface = parsed[0].trim();
                if (iface.startsWith(ComponentConfiguration.COMPONENT_PATH_SEPARATOR)) {
                    // prepend relative path with mount
                    iface = mount + iface;
                }
                final String classPath = parsed[1];
                result.put(iface, RendererPool.get(classPath.trim(), loader));
            } catch (final IndexOutOfBoundsException ex) {
                throw new IllegalArgumentException(
                        "Error by parsing body configuration line for the template configuration. "
                                + DebugUtils.printFields(line));
            } catch (final ClassNotFoundException e) {
                throw new IllegalArgumentException("Fragment class couldn't be found. " + DebugUtils.printFields(line),
                        e);
            }
        }
        return new RenderKitConfiguration(result);
    }

    /**
     * converts this configuration into an dto
     * 
     * @return dto
     */
    public ConfigurationDTO toDTO() {
        final ConfigurationDTO ret = new ConfigurationDTO();
        ret.setConfigurationType(TYPE);
        ret.setMapping(MapEntry.convert(getConfigurationValue()));
        return ret;
    }

}
