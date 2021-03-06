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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.ClimbUp;
import net.xy.jcms.controller.configurations.parser.FragmentXMLParser;
import net.xy.jcms.controller.configurations.pool.TemplatePool;
import net.xy.jcms.persistence.BodyEntry;
import net.xy.jcms.persistence.MapEntry;
import net.xy.jcms.persistence.usecase.ConfigurationDTO;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IFragment;
import net.xy.jcms.shared.compiler.DynamicFragment;
import net.xy.jcms.shared.compiler.DynamicFragment.Element;

/**
 * describes the connection of differend page components/fragements/templates.
 * Template inclusion is performed to an simple stacking of subsequent component
 * tree's or component configuration trees.
 * 
 * @author xyan
 * 
 */
public class TemplateConfiguration extends Configuration<Map<String, IFragment>> {
    /**
     * gloabl type constant for this type
     */
    public static final ConfigurationType TYPE = ConfigurationType.TemplateConfiguration;

    /**
     * default constructor
     * 
     * @param configurationValue
     */
    public TemplateConfiguration(final Map<String, IFragment> configurationValue) {
        super(TYPE, configurationValue);
    }

    /**
     * returns an key associated template deffinition from full component path
     * comp1.comp2.key ans climbup strategy
     * 
     * @param tmplName
     * @return value
     */
    public IFragment get(final String tmplName, final ComponentConfiguration config) {
        return getMatch(tmplName, config).getValue();
    }

    /**
     * stores found config requests to save iteration strategies
     */
    private final Map<String, Match<String, IFragment>> cache = enableCache ? new HashMap<String, Match<String, IFragment>>()
            : null;

    /**
     * returns an key associated template deffinition and where it was found.
     * -full component path comp1.comp2.comp3.key
     * -parents comp1.comp2.key, comp1.key
     * 
     * @param tmplName
     * @param config
     * @return never null
     */
    public Match<String, IFragment> getMatch(final String tmplName, final ComponentConfiguration config) {
        final String fullPathKey = ConfigurationIterationStrategy.fullPath(config, tmplName);
        if (enableCache) {
            final Match<String, IFragment> cached = cache.get(fullPathKey);
            if (cached != null) {
                return cached;
            }
        }
        Match<String, IFragment> value = new Match<String, IFragment>(null, null);
        final ClimbUp strategy = new ClimbUp(config, tmplName);
        for (final String pathKey : strategy) {
            final IFragment found = getConfigurationValue().get(pathKey);
            if (found != null) {
                value = new Match<String, IFragment>(pathKey, found);
                break;
            }
        }
        if (value.getValue() != null) {
            if (enableCache) {
                cache.put(fullPathKey, value);
            }
            return value;
        } else {
            throw new IllegalArgumentException("An mendatory fragment/template definition was not found! "
                    + DebugUtils.printFields(tmplName, fullPathKey));
        }
    }

    /**
     * retrieves an global non component template configuration mainly used to
     * retrieve the root fragment.
     * 
     * @param tmplName
     * @return value
     */
    public IFragment get(final String tmplName) {
        return getConfigurationValue().get(tmplName);
    }

    @Override
    public int hashCode() {
        return getConfigurationValue().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return getConfigurationValue().equals(object);
    }

    @Override
    public TemplateConfiguration mergeConfiguration(final Configuration<Map<String, IFragment>> otherConfig) {
        return mergeConfiguration(otherConfig.getConfigurationValue());
    }

    @Override
    public TemplateConfiguration mergeConfiguration(final Map<String, IFragment> otherConfig) {
        final Map<String, IFragment> result = new HashMap<String, IFragment>(getConfigurationValue());
        result.putAll(otherConfig);
        return new TemplateConfiguration(result);
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
    public static TemplateConfiguration initByString(final String configString, final ClassLoader loader,
            final String mount) {
        final Map<String, IFragment> result = new HashMap<String, IFragment>();
        final String[] lines = configString.split("\n");
        for (final String line : lines) {
            if (StringUtils.isBlank(line) || line.trim().startsWith("#")) {
                continue;
            }
            final String[] parsed = line.trim().split("=", 2);
            try {
                String name = parsed[0].trim();
                if (name.startsWith(ComponentConfiguration.COMPONENT_PATH_SEPARATOR)) {
                    // prepend relative path with mount
                    name = mount + name;
                }
                final String classPath = parsed[1];
                if (classPath.toLowerCase().endsWith(".xml")) {
                    // init template by parsing an xml
                    result.put(name, FragmentXMLParser.parse(classPath, loader));
                } else {
                    // init template by precompiled class
                    result.put(name, TemplatePool.get(classPath.trim(), loader));
                }
            } catch (final IndexOutOfBoundsException ex) {
                throw new IllegalArgumentException(
                        "Error by parsing body configuration line for the template configuration. "
                                + DebugUtils.printFields(line));
            } catch (final ClassNotFoundException e) {
                throw new IllegalArgumentException("Fragment class couldn't be found. " + DebugUtils.printFields(line),
                        e);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Fragment xml couldn't be found. " + DebugUtils.printFields(line), e);
            }
        }
        return new TemplateConfiguration(result);
    }

    /**
     * converts this configuration into an dto
     * 
     * @return dto
     */
    public ConfigurationDTO toDTO() {
        final ConfigurationDTO ret = new ConfigurationDTO();
        ret.setConfigurationType(TYPE);
        final List<MapEntry> mlist = new ArrayList<MapEntry>();
        final List<BodyEntry> blist = new ArrayList<BodyEntry>();
        for (final Entry<String, IFragment> entry : getConfigurationValue().entrySet()) {
            if (entry.getValue() instanceof DynamicFragment) {
                final BodyEntry ent = new BodyEntry();
                ent.setKey(entry.getKey());
                ent.setValue(entry.getValue().getClass().getName());
                final StringBuilder contains = new StringBuilder();
                for (final Element elem : ((DynamicFragment) entry.getValue()).getElementList()) {
                    switch (elem.type) {
                    case Child:
                        contains.append(String.format(FragmentXMLParser.COMPONENTMASK, elem.value, elem.childComponent));
                        break;
                    case Static:
                        contains.append(elem.value);
                        break;
                    case Template:
                        contains.append(String.format(FragmentXMLParser.TEMPLATEMASK, elem.value));
                        break;
                    }
                }
                ent.setContent(contains.toString());
                blist.add(ent);
            } else {
                final MapEntry ent = new MapEntry();
                ent.setKey(entry.getKey());
                ent.setValue(entry.getValue().getClass().getName());
                mlist.add(ent);
            }
        }
        ret.setMapping(mlist);
        ret.setContainment(blist);
        return ret;
    }

    /**
     * returns the list of classes and dynamic fragments used in this config as
     * de/path/to/class removes file ending if present
     * 
     * @return nomarlized source pathes
     */
    public Map<String, String> getSources() {
        final Map<String, String> srcs = new HashMap<String, String>();
        for (final Entry<String, IFragment> entry : getConfigurationValue().entrySet()) {
            if (entry.getValue() instanceof DynamicFragment) {
                String path = ((DynamicFragment) entry.getValue()).getSource();
                if (path.contains(".")) {
                    path = path.substring(0, path.lastIndexOf("."));
                }
                srcs.put(entry.getKey(), path);
            } else {
                srcs.put(entry.getKey(), entry.getValue().getClass().getPackage().getName()
                        .replace(".", File.separator) + File.separator + entry.getValue().getClass().getSimpleName());
            }
        }
        return srcs;
    }

}
