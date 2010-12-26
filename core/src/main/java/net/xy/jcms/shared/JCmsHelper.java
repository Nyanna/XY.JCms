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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLStreamException;
import org.apache.log4j.Logger;
import org.xeustechnologies.jcl.JarClassLoader;

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
                    final ClassLoader cl = this.getClass().getClassLoader();
                    cache = TranslationParser.parse(loadResource(translationConfigXml, cl), cl);
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
                    final ClassLoader cl = this.getClass().getClassLoader();
                    cache = UsecaseParser.parse(loadResource(usecaseConfigurationXml, cl), cl);
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
            InputStream st = null;
            if (loader instanceof JarClassLoader) {
                // fix for jcl only implementing this method
                st = loader.getResourceAsStream(path.trim());
                return st;
            }
            if (st == null) {
                throw new IllegalArgumentException("Resource to load doesn't exists. "
                        + DebugUtils.printFields(path, loader));
            }
        }
        return loadResource(url, loader);
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
    public static InputStream loadResource(final URL url, final ClassLoader loader) throws IOException {
        if (url == null || loader == null) {
            throw new IllegalArgumentException("Fiedls can't be empty.");
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

    /**
     * instantioates and configures an performant deamon threadpool
     * 
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     *            in seconds
     * @return threadpool instance
     */
    public static ExecutorService getThreadPool(final int corePoolSize, final int maximumPoolSize,
            final long keepAliveTime) {
        final ThreadPoolExecutor pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        pool.setThreadFactory(new DeamonFactory());
        return pool;
    }

    /**
     * an simple wrapper class using the default but setting all threads to
     * deamons cuz they can simply abborted on shutdown.
     * 
     * @author Xyan
     */
    private static class DeamonFactory implements ThreadFactory {
        private static final ThreadFactory DEFAULT = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = DEFAULT.newThread(r);
            t.setDaemon(true);
            return t;
        }
    }
}
