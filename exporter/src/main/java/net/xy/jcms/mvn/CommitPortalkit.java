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
package net.xy.jcms.mvn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * processes an specified portalkit structure an commits all classen to classes
 * table
 * 
 * @author Xyan
 * @goal commit
 * 
 */
public class CommitPortalkit extends AbstractMojo {

    /**
     * The root directory to be processed
     * 
     * @parameter
     */
    private File kitRoot;

    /**
     * the url for the sql connection
     * 
     * @parameter
     */
    private String sqlUrl;

    /**
     * Specifies the username for the db connection
     * 
     * @parameter
     */
    private String username;

    /**
     * Specifies the password used for the connection
     * 
     * @parameter
     */
    private String password;

    /**
     * basedir in which the pom resides
     * 
     * @parameter expression="${basedir}"
     */
    private File basedir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Tests successfull commiting to db.");
        getLog().info("Running in: " + basedir);
        getLog().info("Portalkit in: " + kitRoot);
        final File stCl = new File(basedir.getAbsolutePath() + "/target/classes");
        getLog().info("Classpath starting at: " + stCl);
        if (kitRoot == null) {
            throw new MojoFailureException("You have to specify the portal kit root directory.");
        }
        final Connection connection;
        try {
            connection = DriverManager.getConnection(sqlUrl, username, password);
        } catch (final SQLException e) {
            throw new MojoFailureException("Could not etablish an connection to the db.", e);
        }
        final List<File> files = scanForClasses(kitRoot);
        try {
            insertClasses(files, connection, stCl);
        } catch (final SQLException e) {
            throw new MojoFailureException("Could not insert into the db.", e);
        }
    }

    /**
     * scans for all class files under an dir
     * 
     * @param dir
     * @return
     */
    private List<File> scanForClasses(final File dir) {
        final List<File> fileList = new ArrayList<File>();
        for (final File file : dir.listFiles()) {
            if (file.isFile()) {
                if (file.getName().endsWith(".class")) {
                    fileList.add(file);
                }
            } else if (file.isDirectory()) {
                fileList.addAll(scanForClasses(file));
            }
        }
        return fileList;
    }

    /**
     * inserts an list of classfiles into db
     * 
     * @param classes
     * @param connection
     * @param startClasspath
     * @throws SQLException
     */
    private void insertClasses(final List<File> classes, final Connection connection, final File startClasspath)
            throws SQLException {
        for (final File cl : classes) {
            final StringBuilder sql = new StringBuilder("REPLACE INTO Classes SET");
            String classPath = cl.getAbsolutePath().replace(startClasspath.getAbsolutePath(), "")
                    .replace("/", ".").replace("\\", ".");
            if (classPath.startsWith(".")) {
                // remove preceeding dot
                classPath = classPath.substring(1);
            }
            if (classPath.endsWith(".class")) {
                // remove class ending
                classPath = classPath.substring(0, classPath.length() - 6);
            }
            sql.append(" name = '").append(classPath).append("', ");
            sql.append(" bytecode = ?").append(",");
            sql.append(" size = ").append(cl.length());
            sql.append(";");
            // getLog().info("SQL Query: " + sql);

            final PreparedStatement pst = connection.prepareStatement(sql.toString());
            try {
                pst.setBinaryStream(1, new FileInputStream(cl));
            } catch (final FileNotFoundException e) {
                throw new SQLException("File couldn't be opened for reading.", e);
            }

            if (pst.executeUpdate() != 1) {
                continue;
            }
            getLog().info("Added class: " + classPath);
        }
    }

    /**
     * sets the root of the portalkit
     * 
     * @param kitRoot
     */
    public void setKitRoot(final File kitRoot) {
        this.kitRoot = kitRoot;
    }

    /**
     * sets the sql servers url for jdbc
     * 
     * @param sqlUrls
     */
    public void setSqlUrl(final String sqlUrl) {
        this.sqlUrl = sqlUrl;
    }

    /**
     * sets the username used for the db connection
     * 
     * @param username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * sets the password used for the db connection
     * 
     * @param password
     */
    public void setPassword(final String password) {
        this.password = password;
    }
}
