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
package net.xy.jcms.controller.configurations.stores;

/**
 * provides an mechanism to store informations on the client in http it would be
 * an cookie
 * 
 * @author Xyan
 * 
 */
public class ClientStore {
    /**
     * limit in bytes -1 means not limit
     */
    private int limit = -1;

    /**
     * default constructor with deactivated store
     */
    public ClientStore() {
    }

    /**
     * default constructor
     * 
     * @param limit
     *            capacity of bytes an client can store
     */
    public ClientStore(final int limit) {
        this.limit = limit;
    }

    /**
     * gets the maximum limt in bytes an client can store
     * 
     * @return value
     */
    public int getLimit() {
        return limit;
    }

    /**
     * checks if an object would exceeds the capacity left
     * 
     * @param object
     * @return true in case it exceeds or the store is NA
     */
    public boolean exceedsLimit(final Object object) {
        if (getLimit() == -1) {
            return true;
        }
        // TODO [LOW] implement limit check
        return false;
    }

    /**
     * stores an object on the client if afterwards processed by the protocoll
     * adapter.
     * 
     * @param key
     * @param value
     * @throws ClientStoreException
     *             if the store in NA or the value exceeds the limit
     */
    public void store(final String key, final Object value) throws ClientStoreException {
        if (getLimit() == -1) {
            throw new ClientStoreException("Client don't supports storing");
        }
        // TODO [HIGH] storing of Clientstore
    }

    /**
     * gets an key based value from the store. if you need the original request
     * value get it from the controller params.
     * 
     * @param key
     * @return null in case of deactivated or the value
     */
    public Object getValue(final String key) {
        return key;
    }

    /**
     * exception thrown if the store is deactivated/not available or if its
     * limit reached
     * 
     * @author Xyan
     * 
     */
    public static class ClientStoreException extends Exception {
        private static final long serialVersionUID = -3930785534423907623L;

        /**
         * with description
         * 
         * @param string
         */
        public ClientStoreException(final String string) {
            super(string);
        }
    }

}
