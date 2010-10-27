package net.xy.jcms.controller.configurations;

import net.xy.jcms.controller.TranslationConfiguration.TranslationRule;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * adapter which collects the translationrules based on the DataAccesContext
 * 
 * @author xyan
 * 
 */
public interface ITranslationConfigurationAdapter {

    /**
     * return an rulelist
     * 
     * @param dac
     * @return
     */
    public TranslationRule[] getRuleList(final IDataAccessContext dac);
}
