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
package net.xy.jcms.controller.configurations;

import net.xy.jcms.controller.configurations.stores.ClientStore;

public class ControllerConfiguration extends Configuration<Object> {
    public ControllerConfiguration(final ConfigurationType configurationType, final Object configurationValue) {
        super(ConfigurationType.ControllerConfiguration, null);
    }

    @Override
    public void mergeConfiguration(final Configuration<Object> otherConfig) {
        // TODO Auto-generated method stub
    }

    private ClientStore store = new ClientStore();

    /**
     * returns an clientStore
     * 
     * @return value never null
     */
    public ClientStore getClientStore() {
        return store;
    }

    /**
     * sets an new client store
     * 
     * @param store
     */
    public void setClientStore(final ClientStore store) {
        this.store = store;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean equals(final Object object) {
        // TODO Auto-generated method stub
        return false;
    }

}
