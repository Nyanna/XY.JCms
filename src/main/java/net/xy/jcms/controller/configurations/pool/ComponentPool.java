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

import net.xy.jcms.shared.IComponent;

/**
 * manages an cached pool of components
 * 
 * @author Xyan
 * 
 */
public class ComponentPool {
    /**
     * logger
     */
    private final static Logger LOG = Logger.getLogger(TemplatePool.class);

    /**
     * cache pool
     */
    private final static Map<String, IComponent> cachePool = new HashMap<String, IComponent>();

    /**
     * manages an cached pool of loaded fragment instances
     * 
     * @param classPath
     * @return value
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static IComponent get(final String classPath, final ClassLoader loader)
            throws ClassNotFoundException {
        if (cachePool.containsKey(classPath)) {
            return cachePool.get(classPath);
        } else {
            return get((Class<IComponent>) loader.loadClass(classPath));
        }
    }

    /**
     * caches already instantiated component
     * 
     * @param component
     *            class of component
     * @return
     * @throws ClassNotFoundException
     */
    public static IComponent get(final Class<? extends IComponent> component) throws ClassNotFoundException {
        if (cachePool.containsKey(component.getName())) {
            return cachePool.get(component.getName());
        } else {
            if (!IComponent.class.isAssignableFrom(component)) {
                throw new IllegalArgumentException("Component class don't implements IComponent.");
            }

            final IComponent comp;
            try {
                comp = component.newInstance();
            } catch (final InstantiationException e) {
                LOG.error(e);
                throw new ClassNotFoundException("Failure on instantiating Component class " + component.getName());
            } catch (final IllegalAccessException e) {
                LOG.error(e);
                throw new ClassNotFoundException("Failure on accessing and instantiating Component class "
                        + component.getName());
            }

            cachePool.put(component.getName(), comp);
            return comp;
        }
    }
}
