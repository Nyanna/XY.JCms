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
