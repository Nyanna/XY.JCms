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
package net.xy.jcms.shared;

import javax.xml.stream.XMLStreamException;

import net.xy.jcms.controller.TranslationConfiguration;
import net.xy.jcms.controller.UsecaseConfiguration;
import net.xy.jcms.controller.TranslationConfiguration.TranslationRule;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.ITranslationConfigurationAdapter;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.parser.TranslationParser;
import net.xy.jcms.controller.configurations.parser.UsecaseParser;
import net.xy.jcms.controller.configurations.parser.XMLValidator;
import net.xy.jcms.controller.configurations.parser.XMLValidator.XMLValidationException;

/**
 * global helper class with various functions
 * 
 * @author xyan
 * 
 */
public class JCmsHelper {

    /**
     * sets the configuration via two obmitted xml resource names, via thread
     * classloader
     * 
     * @param translationConfigXml
     * @param usecaseConfigurationXml
     * @throws XMLValidationException
     */
    public static void setConfiguration(final String translationConfigXml, final String usecaseConfigurationXml)
            throws XMLValidationException {

        XMLValidator.validate(translationConfigXml);
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

        XMLValidator.validate(usecaseConfigurationXml);
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

    /**
     * gets an configuration out of an configurationlist
     * 
     * @param configuration
     * @return value
     */
    public static Configuration<?> getConfigurationByType(final ConfigurationType type,
            final Configuration<?>[] configuration) {
        for (final Configuration<?> config : configuration) {
            if (type.equals(config.getConfigurationType())) {
                return config;
            }
        }
        return null;
    }
}
