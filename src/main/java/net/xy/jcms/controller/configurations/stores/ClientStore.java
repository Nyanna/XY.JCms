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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * provides an mechanism to store informations on the client in http it would be
 * an cookie
 * 
 * @author Xyan
 * 
 */
public class ClientStore {
    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(ClientStore.class);

    /**
     * limit in bytes -1 means not limit
     */
    private final int limit;

    /**
     * internal store
     */
    private final Map<String, Object> store = new HashMap<String, Object>();

    /**
     * stores the actual used bytes
     */
    private int actualUsage = 0;

    /**
     * stores the type of saving and reading mechanism
     */
    private final Type type;

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!ClientStore.class.isInstance(obj)) {
            return false;
        }
        final ClientStore oo = (ClientStore) obj;
        return limit == oo.limit && actualUsage == oo.actualUsage && type.equals(oo.type) && store.equals(oo.store);
    }

    @Override
    public int hashCode() {
        int hash = 34;
        hash = hash * 3 + limit;
        hash = hash * 3 + actualUsage;
        hash = hash * 3 + type.ordinal();
        hash = hash * 3 + store.hashCode();
        return hash;
    }

    /**
     * triggers the method used for saving the store
     * 
     * @author Xyan
     * 
     */
    public enum Type {
        NONE, ONCLIENT, ONSERVER;
    }

    /**
     * default constructor with deactivated store
     */
    public ClientStore(final Type type) {
        this.type = type;
        limit = -1;
    }

    /**
     * default constructor
     * 
     * @param limit
     *            capacity of bytes an client can store
     */
    public ClientStore(final int limit, final Type type) {
        this.limit = limit;
        this.type = type;
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
     * returns the storage type
     * 
     * @return
     */
    public Type getType() {
        return type;
    }

    /**
     * checks if an object would exceeds the capacity left
     * 
     * @param object
     * @return true in case it exceeds or the store is NA
     */
    public boolean exceedsLimit(final Object object) {
        if (limit == -1) {
            return true;
        }
        if (limit == 0) {
            return false;
        }
        if (object instanceof String) {
            return actualUsage + ((String) object).length() > limit;
        } else if (object instanceof Serializable) {
            // serialization
            final ByteArrayOutputStream st = serialize(object);
            if (st != null) {
                return actualUsage + st.size() > limit;
            }
        } else {
            return actualUsage + object.toString().length() > limit;
        }
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
        if (limit == 0) {
            throw new ClientStoreException("Client don't supports storing");
        }
        if (value instanceof String) {
            if (!(actualUsage + ((String) value).length() > limit)) {
                store.put(key, value);
                actualUsage = actualUsage + ((String) value).length();
                return;
            }
        } else if (value instanceof Serializable) {
            // serialization
            final ByteArrayOutputStream st = serialize(value);
            if (st != null && !(actualUsage + st.size() > limit)) {
                store.put(key, value);
                actualUsage = actualUsage + st.size();
            }
        } else {
            final String objStr = value.toString();
            if (!(actualUsage + objStr.length() > limit)) {
                store.put(key, value);
                actualUsage = actualUsage + objStr.length();
                return;
            }
        }
        throw new ClientStoreException("Object could not be analyzed or converted or stored.");
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
     * returns all object currently in the store
     * 
     * @return an unmodifyable map
     */
    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(store);
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

    /**
     * serializes an object
     * 
     * @param obj
     * @return
     */
    private static ByteArrayOutputStream serialize(final Object obj) {
        final ByteArrayOutputStream st = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream out = new ObjectOutputStream(st);
            out.writeObject(obj);
            out.flush();
        } catch (final IOException e) {
            return null;
        }
        return st;
    }

    /**
     * converts an object to anstring via toString or serialization
     * 
     * @param obj
     * @return
     */
    public static String objectToString(final Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Serializable) {
            // serialization
            final ByteArrayOutputStream st = serialize(obj);
            if (st != null) {
                return st.toString();
            }
        } else {
            return obj.toString();
        }
        return null;
    }

}
