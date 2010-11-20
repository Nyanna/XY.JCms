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
package net.xy.jcms.controller.configurations.pool;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.xy.jcms.shared.IFragment;

/**
 * manages an cached pool of templates
 * 
 * @author Xyan
 * 
 */
public class TemplatePool {
    /**
     * logger
     */
    private final static Logger LOG = Logger.getLogger(TemplatePool.class);

    /**
     * cache pool
     */
    private final static Map<String, IFragment> cachePool = new HashMap<String, IFragment>();

    /**
     * manages an cached pool of loaded fragment instances
     * 
     * @param classPath
     * @return value
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static IFragment get(final String classPath, final ClassLoader loader)
            throws ClassNotFoundException {
        if (cachePool.containsKey(classPath)) {
            return cachePool.get(classPath);
        } else {
            final Object fragment;
            try {
                fragment = loader.loadClass(classPath).newInstance();
            } catch (final InstantiationException e) {
                LOG.error(e);
                throw new ClassNotFoundException("Failure on instantiating Template class " + classPath);
            } catch (final IllegalAccessException e) {
                LOG.error(e);
                throw new ClassNotFoundException("Failure on accessing and instantiating Template class " + classPath);
            }
            if (IFragment.class.isInstance(fragment)) {
                cachePool.put(classPath, (IFragment) fragment);
                return (IFragment) fragment;
            } else {
                throw new IllegalArgumentException("Fragment class don't implements IFragment.");
            }
        }
    }
}
