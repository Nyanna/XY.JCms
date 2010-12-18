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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.xy.jcms.controller.configurations.ITranslationConfigurationAdapter;
import net.xy.jcms.controller.configurations.pool.ConverterPool;
import net.xy.jcms.controller.translation.RuleParameter;
import net.xy.jcms.controller.translation.TranslationRule;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * implements an connector for retrieving the translation configuration from db.
 * The whole rule list shares an classloader for its type converters.
 * 
 * @author Xyan
 * 
 */
public class TranslationDBConnector implements ITranslationConfigurationAdapter {
    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(TranslationDBConnector.class);

    /**
     * holds an singleton connection to the db
     */
    private final Connection connection;

    /**
     * classloader parent for the translation typeconverter classloaders
     */
    private final ClassLoader parentLoader;

    /**
     * cache for storing loading results
     */
    private TranslationRule[] cache;

    /**
     * constructor needs db connection to retrieve translation rules
     * 
     * @param connection
     *            will be set to readonly
     * @param parentLoader
     * @throws SQLException
     */
    public TranslationDBConnector(final Connection connection, final ClassLoader parentLoader) throws SQLException {
        this.connection = connection;
        this.parentLoader = parentLoader;
        connection.setReadOnly(true);
    }

    @Override
    public TranslationRule[] getRuleList(final IDataAccessContext dac) {
        if (dac.getProperty("flushConfig") == null && cache != null) {
            return cache;
        }
        try {
            final ClassLoader loader = new DBClassLoader(connection, parentLoader);
            cache = loadTranslations(dac, connection, loader);
            return cache;
        } catch (final SQLException e) {
            LOG.fatal("Could load translations from DB.", e);
        } catch (final ClassNotFoundException e) {
            LOG.fatal("An mendatory Translation type convertercouldn't be loaded.", e);
        }
        return null;
    }

    /**
     * loads/parses an single translation rule
     * 
     * @param dac
     * @param connection
     * @param parentLoader
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private static TranslationRule[] loadTranslations(final IDataAccessContext dac, final Connection connection,
            final ClassLoader loader) throws SQLException, ClassNotFoundException {
        final Statement query = connection.createStatement();
        if (!query.execute("SELECT * FROM Translations WHERE Enabled = true;")) {
            throw new IllegalArgumentException("Retrieving translations from DB returned no results.");
        }
        final ResultSet result = query.getResultSet();
        final List<TranslationRule> cases = new LinkedList<TranslationRule>();
        while (result.next()) {
            cases.add(loadRule(dac, result, connection, loader));
        }
        return cases.toArray(new TranslationRule[cases.size()]);
    }

    /**
     * gets an rule out from the query resultset
     * 
     * @param dac
     * @param result
     * @param connection
     * @param parentLoader
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private static TranslationRule loadRule(final IDataAccessContext dac, final ResultSet result,
            final Connection connection, final ClassLoader loader) throws SQLException, ClassNotFoundException {
        final String reactOn = result.getString("reactOn");
        final String buildOff = result.getString("buildOff");
        final String usecase = result.getString("usecase");
        // TODO [LOW] implement building mapping support for mapped types as in
        // xml 23234 = funsounds
        final List<RuleParameter> params = parseParameters(result.getString("parameters"), connection, loader);
        return new TranslationRule(reactOn, buildOff, usecase, params);
    }

    /**
     * parses the parameters
     * 
     * @param paramStr
     * @param connection
     * @param loader
     * @return
     * @throws ClassNotFoundException
     */
    private static List<RuleParameter> parseParameters(final String paramStr, final Connection connection,
            final ClassLoader loader) throws ClassNotFoundException {
        if (StringUtils.isBlank(paramStr)) {
            return null;
        }
        final List<RuleParameter> params = new ArrayList<RuleParameter>();
        final String[] lines = paramStr.split("\n");
        for (final String line : lines) {
            final String[] parsLine = line.split(",");
            final String paramName = parsLine[0];
            final int paramGroup = Integer.valueOf(parsLine[1]);
            final String paramConverter = parsLine[2];
            params.add(new RuleParameter(paramName, paramGroup, ConverterPool.get(paramConverter, loader)));
        }
        return params;
    }

}
