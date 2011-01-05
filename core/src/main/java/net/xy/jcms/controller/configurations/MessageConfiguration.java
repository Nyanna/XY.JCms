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
import java.util.Properties;

import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.FullPathOrRoot;
import net.xy.jcms.persistence.MapEntry;
import net.xy.jcms.persistence.usecase.ConfigurationDTO;
import net.xy.jcms.shared.DebugUtils;

/**
 * implements an default property configuration for messages
 * 
 * @author xyan
 * 
 */
public class MessageConfiguration extends AbstractPropertyBasedConfiguration {
    /**
     * gloabl type constant for this type
     */
    public static final ConfigurationType TYPE = ConfigurationType.MessageConfiguration;

    /**
     * default constructor
     * 
     * @param configurationValue
     */
    public MessageConfiguration(final Properties configurationValue) {
        super(TYPE, configurationValue);
    }

    /**
     * get an message text
     * -full component path comp1.comp2.comp3.key
     * -root key
     * 
     * @param key
     * @return value
     */
    public String getMessage(final String key, final ComponentConfiguration config) {
        return getMessageMatch(key, config).getValue();
    }

    /**
     * stores found config requests to save iteration strategies
     */
    private final Map<String, Match<String, String>> cache = enableCache ? new HashMap<String, Match<String, String>>()
            : null;

    /**
     * get an message text and where ist was found, closure:
     * -full component path comp1.comp2.comp3.key
     * -root key
     * 
     * @param key
     * @param config
     * @return value
     */
    public Match<String, String> getMessageMatch(final String key, final ComponentConfiguration config) {
        final String fullPathKey = ConfigurationIterationStrategy.fullPath(config, key);
        if (enableCache) {
            final Match<String, String> cached = cache.get(fullPathKey);
            if (cached != null) {
                return cached;
            }
        }
        Match<String, String> value = new Match<String, String>(null, null);
        final FullPathOrRoot strategy = new FullPathOrRoot(config, key);
        final List<String> retrievalStack = new ArrayList<String>();
        for (final String pathKey : strategy) {
            retrievalStack.add(pathKey);
            final String found = getConfigurationValue().getProperty(pathKey);
            if (found != null) {
                value = new Match<String, String>(pathKey, found);
                break;
            }
        }
        if (value.getValue() != null) {
            if (enableCache) {
                cache.put(fullPathKey, value);
            }
            return value;
        }
        throw new IllegalArgumentException("An mendatory message configuration was missing! "
                    + DebugUtils.printFields(key, retrievalStack));
    }

    /**
     * gets an straight message without path iteration and config processing
     * 
     * @param key
     * @return never null instead it throws an IllegalArgumentException
     *         exception
     */
    public String getMessage(final String key) {
        final String value = getConfigurationValue().getProperty(key);
        if (value != null) {
            return value;
        }
        throw new IllegalArgumentException("An mendatory message configuration was missing! "
                    + DebugUtils.printFields(key));
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
    public static MessageConfiguration initByString(final String configString, final String mount) {
        return new MessageConfiguration(AbstractPropertyBasedConfiguration.initPropertiesByString(configString, mount));
    }

    @Override
    public MessageConfiguration mergeConfiguration(final Configuration<Properties> otherConfig2) {
        return mergeConfiguration(otherConfig2.getConfigurationValue());
    }

    @Override
    public MessageConfiguration mergeConfiguration(final Properties otherConfig2) {
        return new MessageConfiguration(mergeConfiguration(getConfigurationValue(), otherConfig2));
    }

    /**
     * converts this config to an dto
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
