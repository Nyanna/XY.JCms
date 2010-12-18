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
package net.xy.jcms.shared;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.EnumSet;

import javax.xml.stream.XMLStreamException;
import org.apache.log4j.Logger;

import net.xy.jcms.controller.TranslationConfiguration;
import net.xy.jcms.controller.UsecaseConfiguration;
import net.xy.jcms.controller.configurations.ITranslationConfigurationAdapter;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.parser.TranslationDBConnector;
import net.xy.jcms.controller.configurations.parser.TranslationParser;
import net.xy.jcms.controller.configurations.parser.UsecaseDBConnector;
import net.xy.jcms.controller.configurations.parser.UsecaseParser;
import net.xy.jcms.controller.configurations.parser.XMLValidator;
import net.xy.jcms.controller.configurations.parser.XMLValidator.XMLValidationException;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.controller.usecase.Usecase;
import net.xy.jcms.shared.types.Model;

/**
 * global helper class with various functions
 * 
 * @author xyan
 * 
 */
public class JCmsHelper {
    /**
     * logger
     */
    private final static Logger LOG = Logger.getLogger(JCmsHelper.class);

    /**
     * sets the configuration via two obmitted xml resource names, via its
     * current classloader
     * 
     * @param translationConfigXml
     * @param usecaseConfigurationXml
     * @throws XMLValidationException
     */
    public static void setConfiguration(final String translationConfigXml, final String usecaseConfigurationXml)
            throws XMLValidationException {
        setTranslationConfiguration(translationConfigXml);
        setUsecaseConfiguration(usecaseConfigurationXml);
    }

    /**
     * sets only the translation configuration from xml
     * 
     * @param translationConfigXml
     * @throws XMLValidationException
     */
    public static void setTranslationConfiguration(final String translationConfigXml) throws XMLValidationException {
        XMLValidator.validate(translationConfigXml, JCmsHelper.class.getClassLoader());
        TranslationConfiguration.setTranslationAdapter(new ITranslationConfigurationAdapter() {
            TranslationRule[] cache = null;

            @Override
            public TranslationRule[] getRuleList(final IDataAccessContext dac) {
                if (dac.getProperty("flushConfig") == null && cache != null) {
                    return cache;
                }
                Exception ex = null;
                try {
                    final long start = System.currentTimeMillis();
                    final URLConnection con = this.getClass().getClassLoader()
                            .getResource(translationConfigXml).openConnection();
                    con.setUseCaches(false);
                    con.setDefaultUseCaches(false);
                    con.addRequestProperty("seed", new Long(start).toString());
                    cache = TranslationParser.parse(con.getInputStream(), this.getClass().getClassLoader());
                    LOG.info("Parsing and converting of Translationrule xml config tok:  "
                            + new DecimalFormat("###,###,### ms").format((System.currentTimeMillis() - start)));
                } catch (final XMLStreamException e) {
                    ex = e;
                } catch (final IOException e) {
                    ex = e;
                } catch (final ClassNotFoundException e) {
                    ex = e;
                } finally {
                    if (ex != null) {
                        throw new IllegalArgumentException("Error on parsing the TranslationRules.", ex);
                    }
                }
                return cache;
            }
        });
    }

    /**
     * sets only the usecase configuration from xml
     * 
     * @param usecaseConfigurationXml
     * @throws XMLValidationException
     */
    public static void setUsecaseConfiguration(final String usecaseConfigurationXml) throws XMLValidationException {
        XMLValidator.validate(usecaseConfigurationXml, JCmsHelper.class.getClassLoader());
        UsecaseConfiguration.setUsecaseAdapter(new IUsecaseConfigurationAdapter() {
            Usecase[] cache = null;

            @Override
            public Usecase[] getUsecaseList(final IDataAccessContext dac) {
                if (dac.getProperty("flushConfig") == null && cache != null) {
                    return cache;
                }
                Exception ex = null;
                try {
                    final long start = System.currentTimeMillis();
                    final URLConnection con = this.getClass().getClassLoader()
                            .getResource(usecaseConfigurationXml).openConnection();
                    con.setUseCaches(false);
                    con.setDefaultUseCaches(false);
                    con.addRequestProperty("seed", new Long(start).toString());
                    cache = UsecaseParser.parse(con.getInputStream(), this.getClass().getClassLoader());
                    LOG.info("Parsing and converting of Usecase xml config tok:   "
                            + new DecimalFormat("###,###,### ms").format((System.currentTimeMillis() - start)));
                } catch (final XMLStreamException e) {
                    ex = e;
                } catch (final IOException e) {
                    ex = e;
                } catch (final ClassNotFoundException e) {
                    ex = e;
                } finally {
                    if (ex != null) {
                        throw new IllegalArgumentException("Error on parsing the UsecaseConfiguration.", ex);
                    }
                }
                return cache;
            }
        });
    }

    /**
     * filters out specific types of configurations
     * 
     * @param types
     * @param configurations
     * @return an filtered selection of the given configs
     */
    public static Model getConfigurations(final EnumSet<ConfigurationType> types,
            final Model configurations) {
        final Model returnedConfig = new Model();
        for (final ConfigurationType type : types) {
            returnedConfig.put(type, configurations.get(type));
        }
        return returnedConfig;
    }

    /**
     * loads an resource with the loader returns an inputstream and prevents the
     * vm from caching
     * 
     * @param path
     * @param loader
     * @return value
     * @throws IOException
     */
    public static InputStream loadResource(final String path, final ClassLoader loader) throws IOException {
        if (path == null || loader == null) {
            throw new IllegalArgumentException("Fiedls can't be empty.");
        }
        final URL url = loader.getResource(path.trim());
        if (url == null) {
            throw new IllegalArgumentException("Resource to load doesn't exists. "
                    + DebugUtils.printFields(path, loader));
        }
        final URLConnection con = url.openConnection();
        con.setUseCaches(false);
        con.setDefaultUseCaches(false);
        con.addRequestProperty("seed", new Long(System.currentTimeMillis()).toString());
        return con.getInputStream();
    }

    /**
     * sets DB sql connector adaptern for TRanslation and Usecase Configuration
     * using the same readonly connection.
     * 
     * @param sqlUrl
     * @param user
     * @param passwd
     * @throws SQLException
     */
    public static void setDBAdapter(final String sqlUrl, final String user, final String passwd) throws SQLException {
        final Connection connection = DriverManager.getConnection(sqlUrl, user, passwd);
        UsecaseConfiguration.setUsecaseAdapter(new UsecaseDBConnector(connection, JCmsHelper.class.getClassLoader()));
        TranslationConfiguration.setTranslationAdapter(new TranslationDBConnector(connection, JCmsHelper.class
                .getClassLoader()));
    }

    /**
     * sets an sql db adapter for usecases
     * 
     * @param sqlUrl
     * @throws SQLException
     */
    public static void setDBUCLoader(final String sqlUrl, final String user, final String passwd) throws SQLException {
        final Connection connection = DriverManager.getConnection(sqlUrl, user, passwd);
        UsecaseConfiguration.setUsecaseAdapter(new UsecaseDBConnector(connection, JCmsHelper.class.getClassLoader()));
    }

    /**
     * sets an sql adapter for loading translation rules
     * 
     * @param sqlUrl
     * @param user
     * @param passwd
     * @throws SQLException
     */
    public static void setDBRuleLoader(final String sqlUrl, final String user, final String passwd) throws SQLException {
        final Connection connection = DriverManager.getConnection(sqlUrl, user, passwd);
        TranslationConfiguration.setTranslationAdapter(new TranslationDBConnector(connection, JCmsHelper.class
                .getClassLoader()));
    }
}
