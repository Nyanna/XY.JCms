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
package net.xy.jcms.persistence;

import java.util.List;

import net.xy.jcms.controller.TranslationConfiguration;
import net.xy.jcms.controller.UsecaseConfiguration;
import net.xy.jcms.controller.configurations.ITranslationConfigurationAdapter;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.controller.usecase.Usecase;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * connector classes to use with JPA
 * 
 * @author Xyan
 * 
 */
public class DBConnector {

    /**
     * inject the jpa adapter to the framework
     */
    public static void injectAdapter() {
        UsecaseConfiguration.setUsecaseAdapter(new JPAUsecaseAdapter(DBConnector.class.getClassLoader()));
        TranslationConfiguration.setTranslationAdapter(new JPATranslationAdapter(DBConnector.class.getClassLoader()));
    }

    /**
     * adapter for usecases
     * 
     * @author Xyan
     * 
     */
    public static class JPAUsecaseAdapter implements IUsecaseConfigurationAdapter {
        /**
         * stored loader
         */
        private final ClassLoader loader;

        /**
         * default
         * 
         * @param loader
         */
        public JPAUsecaseAdapter(final ClassLoader loader) {
            this.loader = loader;
        }

        @Override
        public Usecase[] getUsecaseList(final IDataAccessContext dac) {
            try {
                final List<Usecase> i = PersistenceHelper.DB.loadAllUsecases(loader);
                return i.toArray(new Usecase[i.size()]);
            } catch (final ClassNotFoundException e) {
                return null;
            }
        }
    }

    /**
     * adapter for translations
     * 
     * @author Xyan
     * 
     */
    public static class JPATranslationAdapter implements ITranslationConfigurationAdapter {
        /**
         * stored loader
         */
        private final ClassLoader loader;

        /**
         * default
         * 
         * @param loader
         */
        public JPATranslationAdapter(final ClassLoader loader) {
            this.loader = loader;
        }

        @Override
        public TranslationRule[] getRuleList(final IDataAccessContext dac) {
            try {
                final List<TranslationRule> i = PersistenceHelper.DB.loadAllTranslation(loader);
                return i.toArray(new TranslationRule[i.size()]);
            } catch (final ClassNotFoundException e) {
                return null;
            }
        }
    }
}
