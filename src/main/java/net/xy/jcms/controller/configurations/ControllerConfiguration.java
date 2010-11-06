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
