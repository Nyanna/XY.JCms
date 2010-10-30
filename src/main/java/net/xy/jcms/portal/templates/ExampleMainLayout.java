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
package net.xy.jcms.portal.templates;

import java.util.Map;

import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.FragmentConfiguration;
import net.xy.jcms.portal.components.BoxComponent;
import net.xy.jcms.portal.components.TextComponent;
import net.xy.jcms.shared.AbstractFragment;
import net.xy.jcms.shared.IOutWriter;

public class ExampleMainLayout extends AbstractFragment {

    @Override
    public FragmentConfiguration getConfiguration() {
        return new FragmentConfiguration(this) {

            @Override
            protected ComponentConfiguration[] prepareChildren(final ContentRepository repository) {
                final ComponentConfiguration maincontent = addComponent("xyx-maincontent", BoxComponent.getInstance());
                final ComponentConfiguration simpletext = maincontent.addComponent("simpleText",
                        TextComponent.getInstance());
                simpletext.setUIConfig("container", "div");
                return null;
            }

            @Override
            protected String[] prepareTemplates(final ContentRepository repository) {
                return null;
            }

            @Override
            protected Map<String, Class<?>> prepareContent() {
                return null;
            }
        };
    }

    @Override
    public void render(final IOutWriter out, final FragmentConfiguration config) {
        config.renderChilds(out);
    }

    private ExampleMainLayout() {
        super(true);
    }

    private final static ExampleMainLayout INSTANCE = new ExampleMainLayout();

    public static ExampleMainLayout getInstance() {
        return INSTANCE;
    }
}
