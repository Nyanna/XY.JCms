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

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * classloader for retrieving components, fragments, renderer from an db.
 * 
 * @author Xyan
 * 
 */
public class DBClassLoader extends ClassLoader {

    /**
     * holds the db connection used for classloading
     */
    private final Connection connection;

    /**
     * cache once loaded classes
     */
    private final Map<String, Class<?>> cache = new HashMap<String, Class<?>>();

    /**
     * constructor needs db connection as param
     * 
     * @param connection
     * @param parent
     */
    public DBClassLoader(final Connection connection, final ClassLoader parent) {
        super(parent);
        if (parent == null) {
            throw new IllegalArgumentException(
                    "ClassLoader is only intended to be inserted in an CL tree parent can't be null.");
        }
        this.connection = connection;
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        return loadClass(name, true);
    }

    @Override
    protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        try {
            // first try parent
            return getParent().loadClass(name);
        } catch (final ClassNotFoundException ex) {
        }

        // parent hasn't that class try to load self
        return loadClassFromDB(name);
    }

    private Class<?> loadClassFromDB(final String name) throws ClassNotFoundException {
        final Class<?> cacheVal = cache.get(name);
        if (cacheVal != null) {
            return cacheVal;
        }

        Statement query;
        try {
            query = connection.createStatement();
            if (query.execute("SELECT * FROM Classes WHERE name = '" + name + "' LIMIT 1;")) {
                final ResultSet result = query.getResultSet();
                if (result.first()) {
                    final Blob code = result.getBlob("bytecode");
                    final Class<?> clazz = defineClass(name, code.getBytes(1, (int) code.length()), 0, (int) code.length());
                    resolveClass(clazz);
                    cache.put(name, clazz);
                    return clazz;
                }
            }
        } catch (final SQLException e) {
            throw new ClassNotFoundException("Loading an class from DB was not possible due an SQL Error.", e);
        }
        throw new ClassNotFoundException("Class couldn't be loaded from parent or db.");
    }
}
