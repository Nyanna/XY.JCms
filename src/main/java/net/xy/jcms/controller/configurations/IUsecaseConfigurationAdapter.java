package net.xy.jcms.controller.configurations;

import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * adapter which collects the usecases based on the dac
 * 
 * @author xyan
 * 
 */
public interface IUsecaseConfigurationAdapter {

    /**
     * returns the usecase list
     * 
     * @param dac
     * @return value
     */
    public Usecase[] getUsecaseList(final IDataAccessContext dac);
}
