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
import java.util.List;

import net.xy.jcms.portal.templates.Empty;
import net.xy.jcms.shared.IFragment;

/**
 * Configuration dummy which only collects the configuration informations and wraps an valid template configuration
 * 
 * @author xyan
 * 
 */
public class TemplateConfigurationDummy extends TemplateConfiguration {
    /**
     * stores requested templates
     */
    private final List<String> tmpls = new ArrayList<String>();

    /**
     * constructor needs an valid template configuration to run down the tree
     * 
     * @param config
     */
    public TemplateConfigurationDummy(final TemplateConfiguration config) {
        super(config.getConfigurationValue());
    }

    @Override
    public IFragment get(final String tmplName, final ComponentConfiguration config) {
        final IFragment ret = super.get(tmplName, config);
        if (ret != null) {
            return ret;
        } else {
            tmpls.add(ConfigurationIterationStrategy.fullPath(config, tmplName));
            return Empty.getInstance();
        }
    }

    /**
     * get the requested templates
     * 
     * @return
     */
    public List<String> getTemplateNames() {
        return tmpls;
    }
}
