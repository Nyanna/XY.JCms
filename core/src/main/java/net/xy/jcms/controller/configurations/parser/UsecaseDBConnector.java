/**
 * This file is part of XY.JCms, Copyright 2010 (C) Xyan Kruse, Xyan@gmx.net, Xyan.kilu.de
 * 
 * XY.JCms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * XY.JCms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with XY.JCms. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xy.jcms.controller.configurations.parser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.xy.jcms.controller.UsecaseConfiguration.Controller;
import net.xy.jcms.controller.UsecaseConfiguration.Parameter;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.controller.configurations.IUsecaseConfigurationAdapter;
import net.xy.jcms.controller.configurations.pool.ControllerPool;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * adapter do load and retrieve usescases from an db
 * 
 * @author Xyan
 * 
 */
public class UsecaseDBConnector implements IUsecaseConfigurationAdapter {
    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(UsecaseDBConnector.class);

    /**
     * holds an singleton connection to the db
     */
    private final Connection connection;

    /**
     * classloader parend for the usecase classloaders
     */
    private final ClassLoader loader;

    /**
     * cache for storing loading results
     */
    private Usecase[] ucCache;

    /**
     * constructor needs dm connection to retrieve usescases
     * 
     * @param connection
     * @throws SQLException
     */
    public UsecaseDBConnector(final Connection connection, final ClassLoader loader) throws SQLException {
        this.connection = connection;
        this.loader = loader;
        connection.setReadOnly(true);
    }

    @Override
    public Usecase[] getUsecaseList(final IDataAccessContext dac) {
        if (ucCache != null) {
            return ucCache;
        }
        try {
            ucCache = loadUCs(dac, connection, loader);
            return ucCache;
        } catch (final SQLException e) {
            LOG.fatal("Could load usecases from DB.", e);
        } catch (final ClassNotFoundException e) {
            LOG.fatal("An mendatory Usecase component couldn't be loaded.", e);
        }
        return null;
    }

    /**
     * loads the usecases from the specified connection
     * 
     * @param dac
     * @param connection
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private static Usecase[] loadUCs(final IDataAccessContext dac, final Connection connection, final ClassLoader loader)
            throws SQLException, ClassNotFoundException {
        final Statement query = connection.createStatement();
        if (!query.execute("SELECT * FROM Usecases WHERE Enabled = true;")) {
            throw new IllegalArgumentException("Retrieving usecases from DB returned no results.");
        }
        final ResultSet result = query.getResultSet();
        final List<Usecase> cases = new LinkedList<Usecase>();
        while (result.next()) {
            cases.add(loadUC(dac, result, connection, loader));
        }
        return cases.toArray(new Usecase[cases.size()]);
    }

    /**
     * loads an single usecase from an DB row
     * 
     * @param dac
     * @param result
     * @param connection
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    static private Usecase loadUC(final IDataAccessContext dac, final ResultSet result, final Connection connection,
            final ClassLoader parentLoader) throws SQLException, ClassNotFoundException {
        // Usecase's own classloader newly instantiated all the usecase need if
        // not already loaded
        final ClassLoader loader = new DBClassLoader(connection, parentLoader);
        final String uniqueId = result.getString("uniqueId");
        final String id = result.getString("id");
        final String description = result.getString("description");
        final Parameter[] parameterList = parseParameters(result.getString("params"));
        final Controller[] controllerList = parseControllers(result.getString("controller"), loader);
        final Configuration<?>[] configurationList = parseConfigurations(uniqueId, connection, loader);
        return new Usecase(id, description, parameterList, controllerList, configurationList);
    }

    /**
     * parses an comma and newline separated list of parameters in the form of:
     * name,type
     * 
     * @param string
     * @return
     */
    private static Parameter[] parseParameters(final String res) {
        if (StringUtils.isBlank(res)) {
            return null;
        }
        final List<Parameter> params = new ArrayList<Parameter>();
        final String[] lines = res.split("\n");
        for (final String line : lines) {
            final String[] parsLine = line.split(",");
            final String paramName = parsLine[0];
            final String paramType = parsLine[1];
            params.add(new Parameter(paramName, paramType));
        }
        return params.toArray(new Parameter[params.size()]);
    }

    /**
     * parses an comma and newline separated list of Controllers in the form of:
     * java.lang.XController,Params,ControllerConfig,ContentRepository
     * 
     * @param res
     * @return
     * @throws ClassNotFoundException
     */
    private static Controller[] parseControllers(final String res, final ClassLoader loader) throws ClassNotFoundException {
        final List<Controller> ctrls = new ArrayList<Controller>();
        final String[] lines = res.split("\n");
        for (final String line : lines) {
            final String[] parsLine = line.split(",");
            final EnumSet<ConfigurationType> omConfigs = EnumSet.noneOf(ConfigurationType.class);
            String ctrlClassName = null;
            for (int i = 0; i < parsLine.length; i++) {
                if (i == 0) {
                    ctrlClassName = parsLine[i];
                } else {
                    omConfigs.add(ConfigurationType.valueOf(parsLine[i]));
                }
            }
            ctrls.add(new Controller(ControllerPool.get(ctrlClassName, loader), omConfigs));
        }
        return ctrls.toArray(new Controller[ctrls.size()]);
    }

    /**
     * loads the configurations from an extra table and uses the obmitted loader
     * to load dependencies.
     * 
     * @param uniqueId
     *            to lookup the configs
     * @param connection
     * @param loader
     * @return
     * @throws SQLException
     */
    private static Configuration<?>[] parseConfigurations(final String uniqueId, final Connection connection,
            final ClassLoader loader) throws SQLException {
        final Statement query = connection.createStatement();
        if (!query.execute("SELECT * FROM Configurations WHERE usecaseUniqueId = " + uniqueId + ";")) {
            // no configurations defined
            LOG.warn("There are no configurations defined for usecase unique id: " + uniqueId);
            return new Configuration[] {};
        }
        final ResultSet result = query.getResultSet();
        final List<Configuration<?>> cases = new LinkedList<Configuration<?>>();
        while (result.next()) {
            final String include = result.getString("include");
            final ConfigurationType type = ConfigurationType.valueOf(result.getString("type"));
            if (StringUtils.isNotBlank(include)) {
                cases.add(Configuration.initConfigurationByInclude(type, include, loader));
            } else {
                cases.add(Configuration.initByString(type, result.getString("value"), loader));
            }
        }
        return cases.toArray(new Configuration[cases.size()]);
    }

}
