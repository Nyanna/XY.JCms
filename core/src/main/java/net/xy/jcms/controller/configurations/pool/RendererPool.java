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

import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IRenderer;

/**
 * manages an cached pool of renderers
 * 
 * @author Xyan
 * 
 */
public class RendererPool {
    /**
     * logger
     */
    private final static Logger LOG = Logger.getLogger(RendererPool.class);

    /**
     * cache pool
     */
    private final static Map<String, IRenderer> cachePool = new HashMap<String, IRenderer>();

    /**
     * manages an cached pool of loaded renderer instances
     * 
     * @param classPath
     * @return value
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static IRenderer get(final String classPath, final ClassLoader loader)
            throws ClassNotFoundException {
        if (cachePool.containsKey(classPath)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Renderer retrieved from cache. " + DebugUtils.printFields(classPath));
            }
            return cachePool.get(classPath);
        } else {
            final Object renderer;
            try {
                renderer = loader.loadClass(classPath).newInstance();
            } catch (final InstantiationException e) {
                LOG.error(e);
                throw new ClassNotFoundException("Failure on instantiating Renderer class " + classPath);
            } catch (final IllegalAccessException e) {
                LOG.error(e);
                throw new ClassNotFoundException("Failure on accessing and instantiating Renderer class " + classPath);
            }
            if (IRenderer.class.isInstance(renderer)) {
                if (LOG.isDebugEnabled()) {
                    // only log infor when debug
                    LOG.info("Renderer new instantiated. " + DebugUtils.printFields(classPath));
                }
                cachePool.put(classPath, (IRenderer) renderer);
                return (IRenderer) renderer;
            } else {
                throw new IllegalArgumentException("Renderer class don't implements IRenderer.");
            }
        }
    }
}
