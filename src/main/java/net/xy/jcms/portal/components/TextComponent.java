/**
 *  This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 *
 *  XY.JCms is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XY.JCms is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XY.JCms.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.portal.components;

import net.xy.jcms.controller.configurations.ContentRepository;
import net.xy.jcms.controller.configurations.UIConfiguration.UI;
import net.xy.jcms.portal.renderer.ITextRenderer;
import net.xy.jcms.shared.AbstractComponent;
import net.xy.jcms.shared.ComponentConfiguration;
import net.xy.jcms.shared.IRenderer;
import net.xy.jcms.shared.OutWriterImplementationAdapter;

public class TextComponent extends AbstractComponent {

    @Override
    public ComponentConfiguration getConfiguration(final String id, final ComponentConfiguration parent) {
        return new ComponentConfiguration(id, this, parent) {
            @Override
            protected String[] prepareMessages() {
                return new String[] { "text" };
            }

            @SuppressWarnings("rawtypes")
            @Override
            protected UI[] prepareUIConfig() {
                return new UI[] {//
                new UI<Boolean>("renderHidden", Boolean.FALSE),//
                        new UI<String>("container", "div", false) };
            }

            @SuppressWarnings("unchecked")
            @Override
            protected Class<? extends IRenderer>[] prepareRenderers() {
                return new Class[] { ITextRenderer.class };
            }

            @Override
            protected String[] prepareTemplates(final ContentRepository repository) {
                return null;
            }

            @Override
            protected ComponentConfiguration[] prepareChildren(final ContentRepository repository) {
                return null;
            }
        };
    }

    @Override
    public void render(final OutWriterImplementationAdapter out, final ComponentConfiguration config) {
        final ITextRenderer r = (ITextRenderer) config.getRenderer(ITextRenderer.class);
        out.append(r.renderText(new StringBuilder(config.getMessage("text"))));
    }

    private TextComponent() {
    }

    private final static TextComponent INSTANCE = new TextComponent();

    public static TextComponent getInstance() {
        return INSTANCE;
    }

}
