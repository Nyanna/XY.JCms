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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xeustechnologies.jcl.JarClassLoader;

/**
 * classloader wrapper for cli solving some problems with long classpathes on
 * windows
 * 
 * @author xyan
 * 
 */
public abstract class CLIClassLoader {
    /**
     * logger
     */
    private static final Logger LOG = Logger.getLogger(CLIClassLoader.class);

    static {
        LOG.info("CLIClassLoader was loaded by " + CLIClassLoader.class.getClassLoader().getClass().getName());
        System.setProperty("jcl.isolateLogging", "false");
    }

    /**
     * default constructor starts program
     * 
     * @param args
     * @throws Throwable
     */
    public CLIClassLoader(final String[] args) throws Throwable {
        main(args);
    }

    /**
     * initializes jcl and the entry class
     * 
     * @param args
     * @throws Throwable
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void main(final String[] args) throws Throwable {
        final File libDir = new File("../lib/");
        final File[] libFiles = libDir.listFiles();
        final List<URL> files = new LinkedList<URL>();
        // append actual dir
        files.add(new File("./").toURI().toURL());
        if (libFiles != null && libFiles.length > 0) {
            for (final File file : libFiles) {
                if (file.getName().matches("log4j-[0-9]+.[0-9]+.[0-9]+.jar")) {
                    continue;
                }
                if (file.getName().endsWith(".jar")) {
                    files.add(file.toURI().toURL());
                }
            }
        }

        final JarClassLoader jcl = new JarClassLoader(files.toArray(new URL[files.size()]));

        try {
            LOG.info("Invoking main method.");
            final Class mainCl = jcl.loadClass(getEntryClass());
            LOG.info("JarLoader got " + mainCl.getName() + " with " + mainCl.getClassLoader().getClass().getName());

            final Method main = mainCl.getMethod("main", String[].class);
            main.invoke(null, (Object) args);
        } catch (final InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

    /**
     * get the name of the programms entry class
     * 
     * @return
     */
    protected abstract String getEntryClass();
}
