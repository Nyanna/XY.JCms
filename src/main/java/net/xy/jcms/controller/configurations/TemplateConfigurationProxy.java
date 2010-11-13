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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.xy.jcms.portal.templates.Empty;
import net.xy.jcms.shared.IFragment;

/**
 * Configuration dummy which only collects the configuration informations and
 * wraps an valid template configuration
 * 
 * @author xyan
 * 
 */
public class TemplateConfigurationProxy extends TemplateConfiguration {
    /**
     * stores missing requested templates
     */
    private final List<String> missingTmpls = new ArrayList<String>();

    /**
     * stores present requested templates
     */
    private final Map<String, String> presentTmpls = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * constructor needs an valid template configuration to run down the tree
     * 
     * @param config
     */
    public TemplateConfigurationProxy(final TemplateConfiguration config) {
        super(config.getConfigurationValue());
    }

    @Override
    public IFragment get(final String tmplName, final ComponentConfiguration config) {
        Match<String, IFragment> value = new Match<String, IFragment>(null, null);
        try {
            value = super.getMatch(tmplName, config);
        } catch (final IllegalArgumentException ex) {
        }
        if (value.getValue() != null) {
            presentTmpls.put(value.getPath(), value.getValue().getClass()
                    .getName());
            return value.getValue();
        } else {
            final String full = ConfigurationIterationStrategy.fullPath(config, tmplName);
            if (!missingTmpls.contains(full)) {
                missingTmpls.add(full);
            }
            return Empty.getInstance();
        }
    }

    /**
     * get the requested templates
     * 
     * @return
     */
    public Map<String, String> getTemplateNames() {
        final Map<String, String> merge = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        merge.putAll(presentTmpls);
        for (final String key : missingTmpls) {
            merge.put(key, "");
        }
        return merge;
    }

    /**
     * get only missing template names
     * 
     * @return
     */
    public List<String> getMissingTemplateNames() {
        Collections.sort(missingTmpls, String.CASE_INSENSITIVE_ORDER);
        return missingTmpls;
    }

    /**
     * gets already configured template names
     * 
     * @return
     */
    public Map<String, String> getPresentTemplates() {
        return presentTmpls;
    }

    /**
     * returns true if a config is missing
     * 
     * @return
     */
    public boolean isMissing() {
        return missingTmpls.isEmpty() ? false : true;
    }
}
