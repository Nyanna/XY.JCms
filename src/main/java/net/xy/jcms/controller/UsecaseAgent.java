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
package net.xy.jcms.controller;

import java.util.EnumSet;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.UsecaseConfiguration.Controller;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.shared.IDataAccessContext;

/**
 * Agent determing the usecase from an KeyChain.
 * 
 * @author xyan
 * 
 */
public class UsecaseAgent {
    /**
     * usecase to last try when no usecases could be found
     */
    private static final String ERROR_USECASE_ID = "ERROR";

    /**
     * exception marker
     * 
     * @author Xyan
     * 
     */
    public static class NoUsecaseFound extends Exception {
        private static final long serialVersionUID = -5460169217069698404L;
    }

    /**
     * searches for an appropriated usecase
     * 
     * @return never returns null
     * @throws NoUsecaseFound
     *             when even no error usecase could be found
     */
    public static Usecase findUsecaseForStruct(final NALKey struct, final IDataAccessContext dac) throws NoUsecaseFound {
        final Usecase foundCase = UsecaseConfiguration.findUsecaseForStruct(struct, dac);
        if (foundCase == null) {
            // try to find most general error usecase
            final Usecase foundErrorCase = UsecaseConfiguration.findUsecaseForStruct(new NALKey(ERROR_USECASE_ID, struct),
                    dac);
            if (foundErrorCase == null) {
                throw new NoUsecaseFound();
            }
            return foundErrorCase;
        }
        return foundCase;
    }

    /**
     * executes all data aggregation processing and controller logic
     * 
     * @param usecase
     * @return null if anything goes right an new usecase if there should be an
     *         redirect
     * @throws ClassNotFoundException
     */
    public static NALKey executeController(final Usecase usecase, final IDataAccessContext dac)
            throws ClassNotFoundException {
        final Controller[] list = usecase.getControllerList();
        for (final Controller controller : list) {
            final EnumSet<ConfigurationType> types = controller.getObmitedConfigurations().clone();
            types.addAll(ConfigurationType.CONTROLLERAPPLICABLE);
            final Configuration<?>[] configs = usecase.getConfigurationList(types);
            final NALKey forward = controller.invoke(dac, configs);
            if (forward != null) {
                return forward;
            }
        }
        return null;
    }

    /**
     * These methods implements the caching of view output based on an hashing
     * alghorythm applied on the Model.
     * 
     * @param usecase
     * @return null or the cached ouput
     */
    public static String applyCaching(final Configuration<?>[] configs) {
        for (final Configuration<?> config : configs) {
            // TODO [LOW] implement ouput caching and configuration hashing
        }
        return null;
    }
}
