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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import net.xy.jcms.controller.configurations.ConfigurationIterationStrategy.ClimbUp;
import net.xy.jcms.controller.configurations.parser.FragmentXMLParser;
import net.xy.jcms.controller.configurations.pool.TemplatePool;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IFragment;
import net.xy.jcms.shared.JCmsHelper;

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
     * returns an key associated template deffinition and where it was found.
     * -full component path comp1.comp2.comp3.key
     * -parents comp1.comp2.key, comp1.key
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
    public void mergeConfiguration(final Configuration<Map<String, IFragment>> otherConfig) {
        getConfigurationValue().putAll(otherConfig.getConfigurationValue());
    }

    /**
     * creates an config based on parsing an string
     * 
     * @param configString
     * @return value
     */
    public static TemplateConfiguration initByString(final String configString, final ClassLoader loader) {
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
                if (classPath.toLowerCase().endsWith(".xml")) {
                    // init template by parsing an xml
                    result.put(name.trim(), FragmentXMLParser.parse(JCmsHelper.loadResource(classPath, loader), loader));
                } else {
                    // init template by precompiled class
                    result.put(name.trim(), TemplatePool.get(classPath.trim(), loader));
                }
            } catch (final IndexOutOfBoundsException ex) {
                throw new IllegalArgumentException(
                        "Error by parsing body configuration line for the template configuration. "
                                + DebugUtils.printFields(line));
            } catch (final ClassNotFoundException e) {
                throw new IllegalArgumentException("Fragment class couldn't be found. " + DebugUtils.printFields(line), e);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Fragment xml couldn't be found. " + DebugUtils.printFields(line), e);
            }
        }
        return new TemplateConfiguration(result);
    }

}
