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
package net.xy.jcms.controller.usecase;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * this pool controls danymic controller loading, reloading and singleton
 * instantiation of the controllers
 * 
 * @author Xyan
 * 
 */
public class ControllerPool {
    /**
     * logger
     */
    static final Logger LOG = Logger.getLogger(ControllerPool.class);

    /**
     * instancelist of the controllers
     */
    final static Map<String, IController> controllers = new HashMap<String, IController>();

    /**
     * loads and caches or returns the controller
     * 
     * @param id
     * @param loader
     * @return
     * @throws ClassNotFoundException
     */
    public static IController get(final String id, final ClassLoader loader) throws ClassNotFoundException {
        if (controllers.containsKey(id)) {
            return controllers.get(id);
        } else {
            final Object instance;
            try {
                final Class<?> object = loader.loadClass(id);
                instance = object.newInstance();
            } catch (final InstantiationException e) {
                LOG.error(e);
                throw new ClassNotFoundException("Failure on instantiating Controller class " + id);
            } catch (final IllegalAccessException e) {
                LOG.error(e);
                throw new ClassNotFoundException("Failure on accessing and instantiating Controller class " + id);
            }
            if (IController.class.isInstance(instance)) {
                controllers.put(id, (IController) instance);
                return controllers.get(id);
            } else {
                throw new ClassNotFoundException("Controller " + id + " doesn't implement IController interface",
                        new ClassCastException());
            }
        }
    }
}
