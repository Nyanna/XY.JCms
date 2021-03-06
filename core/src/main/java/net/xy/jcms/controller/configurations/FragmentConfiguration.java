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

import net.xy.jcms.controller.configurations.UIConfiguration.UI;
import net.xy.jcms.shared.IFragment;
import net.xy.jcms.shared.IRenderer;

/**
 * fragment configuratio scheme. an fragment is almost an component except of the following restriktions:
 * -an fragment can't own message or ui configuration and can't request renderers
 * 
 * @author Xyan
 * 
 */
public abstract class FragmentConfiguration extends ComponentConfiguration {

    /**
     * default constructor
     * 
     * @param name
     * @param parent
     */
    protected FragmentConfiguration(final IFragment compInstance) {
        super(compInstance);
    }

    /**
     * an fragment is not allowed to own an message configuration
     */
    @Override
    final protected String[] prepareMessages() {
        return null;
    }

    /**
     * an fragment is not allowed to own an ui configuration
     */
    @Override
    protected UI<?>[] prepareUIConfig() {
        return null;
    }

    /**
     * an fragment is not allowed to request or use renderers
     */
    @Override
    protected Class<? extends IRenderer>[] prepareRenderers() {
        return null;
    }

    @Override
    public void setUIConfig(final String key, final Object value) {
        throw new IllegalArgumentException("Setting an UI config on an Template/Fragment is not supported!");
    }

    @Override
    public String getMessage(final String key) {
        throw new UnsupportedOperationException("Method is not supported for an fragment configuration.");
    }

    @Override
    public Object getRenderer(final Class<? extends IRenderer> rIface) {
        throw new UnsupportedOperationException("Method is not supported for an fragment configuration.");
    }

    @Override
    public Object getUIConfig(final String key) {
        throw new UnsupportedOperationException("Method is not supported for an fragment configuration.");
    }

    @Override
    public Boolean getUIConfigBoolean(final String key) {
        throw new UnsupportedOperationException("Method is not supported for an fragment configuration.");
    }

    @Override
    public Integer getUIConfigInteger(final String key) {
        throw new UnsupportedOperationException("Method is not supported for an fragment configuration.");
    }

    @Override
    public Long getUIConfigLong(final String key) {
        throw new UnsupportedOperationException("Method is not supported for an fragment configuration.");
    }

    @Override
    public String getUIConfigString(final String key) {
        throw new UnsupportedOperationException("Method is not supported for an fragment configuration.");
    }
}
