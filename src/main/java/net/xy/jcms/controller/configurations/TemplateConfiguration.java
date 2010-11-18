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

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.ClimbUp;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IFragment;

/**
 * describes the connection of differend page components/fragements/templates. Template inclusion is performed to an
 * simple stacking of subsequent component tree's or component configuration trees.
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
     * returns an key associated template deffinition
     * 
     * @param tmplName
     * @return value
     */
    public IFragment get(final String tmplName, final ComponentConfiguration config) {
        return getMatch(tmplName, config).getValue();
    }

    /**
     * returns an key associated template deffinition and where it was found
     * 
     * @param tmplName
     * @param config
     * @return never null
     */
    public Match<String, IFragment> getMatch(final String tmplName, final ComponentConfiguration config) {
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
            return value;
        } else {
            throw new IllegalArgumentException("An mendatory fragment/template definition was not found!");
        }
    }

    /**
     * retrieves an global non component template configuration mainly used to retrieve the root fragment.
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
    public void mergeConfiguration(final Configuration<Map<String, IFragment>> otherConfig) {
        getConfigurationValue().putAll(otherConfig.getConfigurationValue());
    }

    /**
     * creates an config based on parsing an string
     * 
     * @param configString
     * @return value
     */
    public static TemplateConfiguration initByString(final String configString) {
        final Map<String, IFragment> result = new HashMap<String, IFragment>();
        final String[] lines = configString.split("\n");
        for (final String line : lines) {
            if (StringUtils.isBlank(line) || line.trim().startsWith("#")) {
                continue;
            }
            final String[] parsed = line.trim().split("=", 2);
            try {
                final String name = parsed[0];
                final String classPath = parsed[1];
                result.put(name.trim(), fragmentCachePool(classPath.trim()));
            } catch (final IndexOutOfBoundsException ex) {
                throw new IllegalArgumentException(
                        "Error by parsing body configuration line for the template configuration. "
                                + DebugUtils.printFields(line));
            } catch (final ClassNotFoundException e) {
                throw new IllegalArgumentException("Fragment class couldn't be found. " + DebugUtils.printFields(line));
            } catch (final InstantiationException e) {
                throw new IllegalArgumentException("Fragment class couldn't be instantiated. "
                        + DebugUtils.printFields(line));
            } catch (final IllegalAccessException e) {
                throw new IllegalArgumentException("Fragment class couldn't be instantiated. "
                        + DebugUtils.printFields(line));
            }
        }
        return new TemplateConfiguration(result);
    }

    /**
     * cache pool
     */
    private final static Map<String, IFragment> cachePool = new HashMap<String, IFragment>();

    /**
     * manages an cached pool of loaded fragment instances
     * 
     * @param classPath
     * @return value
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static IFragment fragmentCachePool(final String classPath) throws InstantiationException,
            IllegalAccessException,
            ClassNotFoundException {
        if (cachePool.containsKey(classPath)) {
            return cachePool.get(classPath);
        } else {
            // TODO [LOW] replace with callers classloader
            final Object fragment = TemplateConfiguration.class.getClassLoader().loadClass(classPath).newInstance();
            if (IFragment.class.isInstance(fragment)) {
                cachePool.put(classPath, (IFragment) fragment);
                return (IFragment) fragment;
            } else {
                throw new IllegalArgumentException("Fragment class don't implements IFragment.");
            }
        }
    }
}
