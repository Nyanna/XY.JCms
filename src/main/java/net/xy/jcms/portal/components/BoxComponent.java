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
package net.xy.jcms.portal.components;

import java.util.Map;

import net.xy.jcms.controller.configurations.ComponentConfiguration;
import net.xy.jcms.controller.configurations.UIConfiguration.UI;
import net.xy.jcms.portal.renderer.IBoxRenderer;
import net.xy.jcms.shared.AbstractComponent;
import net.xy.jcms.shared.IRenderer;
import net.xy.jcms.shared.IOutWriter;

/**
 * standard box component
 * 
 * @author Xyan
 * 
 */
public class BoxComponent extends AbstractComponent {

    @Override
    public ComponentConfiguration getConfiguration() {
        return new ComponentConfiguration(this) {

            @Override
            protected UI<?>[] prepareUIConfig() {
                return new UI<?>[] { UI_STYLECLASS };
            }

            @Override
            protected String[] prepareTemplates(final Map<String, Object> content) {
                return null;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected Class<? extends IRenderer>[] prepareRenderers() {
                return new Class[] { IBoxRenderer.class };
            }

            @Override
            protected String[] prepareMessages() {
                return null;
            }

            @Override
            protected ComponentConfiguration[] prepareChildren(final Map<String, Object> content) {
                return null;
            }

            @Override
            protected Map<String, Class<?>> prepareContent() {
                return null;
            }
        };
    }

    @Override
    public void render(final IOutWriter out, final ComponentConfiguration config) {
        final IBoxRenderer boxr = (IBoxRenderer) config.getRenderer(IBoxRenderer.class);
        out.append(boxr.renderBegin((String) config.getUIConfig("styleClass")));
        config.renderChilds(out);
        out.append(boxr.renderEnd());
    }

    private BoxComponent() {
        super(true);
    }

    private final static BoxComponent INSTANCE = new BoxComponent();

    public static BoxComponent getInstance() {
        return INSTANCE;
    }

}
