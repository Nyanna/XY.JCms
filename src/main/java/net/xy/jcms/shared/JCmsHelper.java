package net.xy.jcms.shared;

import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import net.xy.jcms.controller.TranslationConfiguration;
import net.xy.jcms.controller.UsecaseConfiguration;
import net.xy.jcms.controller.TranslationConfiguration.TranslationRule;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.ITranslationConfigurationAdapter;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.configurations.parser.TranslationParser;
import net.xy.jcms.controller.configurations.parser.UsecaseParser;

/**
 * global helper class with various functions
 * 
 * @author xyan
 * 
 */
public class JCmsHelper {

    /**
     * sets the configuration via two obmitted xml resource names, via thread classloader
     * 
     * @param translationConfigXml
     * @param usecaseConfigurationXml
     */
    public static void setConfiguration(final String translationConfigXml, final String usecaseConfigurationXml) {

        TranslationConfiguration.setTranslationAdapter(new ITranslationConfigurationAdapter() {
            TranslationRule[] cache = null;

            @Override
            public TranslationRule[] getRuleList(final IDataAccessContext dac) {
                if (cache != null) {
                    return cache;
                }
                try {
                    cache = TranslationParser.parse(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream(translationConfigXml));
                } catch (final XMLStreamException e) {
                    e.printStackTrace();
                }
                return cache;
            }
        });

        UsecaseConfiguration.setUsecaseAdapter(new IUsecaseConfigurationAdapter() {
            Usecase[] cache = null;

            @Override
            public Usecase[] getUsecaseList(final IDataAccessContext dac) {
                if (cache != null) {
                    return cache;
                }
                try {
                    cache = UsecaseParser.parse(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream(usecaseConfigurationXml));
                } catch (final XMLStreamException e) {
                    e.printStackTrace();
                }
                return cache;
            }
        });
    }
}
