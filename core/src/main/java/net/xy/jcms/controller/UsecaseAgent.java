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
package net.xy.jcms.controller;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import net.xy.jcms.controller.NavigationAbstractionLayer.NALKey;
import net.xy.jcms.controller.UsecaseConfiguration.Controller;
import net.xy.jcms.controller.UsecaseConfiguration.Parameter;
import net.xy.jcms.controller.UsecaseConfiguration.Usecase;
import net.xy.jcms.controller.configurations.Configuration;
import net.xy.jcms.controller.configurations.Configuration.ConfigurationType;
import net.xy.jcms.shared.DebugUtils;
import net.xy.jcms.shared.IDataAccessContext;
import net.xy.jcms.shared.JCmsHelper;
import net.xy.jcms.shared.cache.XYCache;
import net.xy.jcms.shared.types.Model;

/**
 * Agent determing the usecase from an NALKey.
 * 
 * @author xyan
 * 
 */
public class UsecaseAgent {
    /**
     * logger
     */
    private static final Logger LOG = Logger.getLogger(UsecaseAgent.class);

    /**
     * usecase to last try when no usecases could be found
     */
    private static final String ERROR_USECASE_ID = "ERROR";

    /**
     * cache instance used for caching the usecase output
     */
    private static final String USECASE_OUPUT_CACHE_ID = "USECASE_OUPUT_CACHE_ID";

    /**
     * cache region used for caching the usecase output
     */
    private static final String CACHE_REGION = "USECASE_CACHE";

    /**
     * exception marker
     * 
     * @author Xyan
     * 
     */
    public static class NoUsecaseFound extends Exception {
        public NoUsecaseFound(final String string) {
            super(string);
        }

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
            final Usecase foundErrorCase = UsecaseConfiguration.findUsecaseForStruct(new NALKey(ERROR_USECASE_ID,
                    struct),
                    dac);
            if (foundErrorCase == null) {
                throw new NoUsecaseFound("No usecase were found for the request. " + DebugUtils.printFields(struct));
            }
            return foundErrorCase;
        }
        LOG.info("Found usecase: " + DebugUtils.printFields(struct));
        return foundCase;
    }

    /**
     * specifies global parameters for all usecases to be copied
     */
    private static final List<String> globals = Arrays.asList(new String[] { "cache" });

    /**
     * creates an destinct NALKey for caching. It checks by which NAL params the
     * usecase was found and will delete params not relevant for the usecase.
     * 
     * @param usecase
     *            which was found for the
     * @param foundFor
     *            nalkey
     * @return value
     */
    public static NALKey destinctCacheKey(final Usecase usecase, final NALKey foundFor) {
        final Map<Object, Object> relevant = new HashMap<Object, Object>();
        for (final Parameter param : usecase.getParameterList()) {
            relevant.put(param.getParameterKey(), foundFor.getParameter(param.getParameterKey()));
        }
        for (final String param : globals) {
            relevant.put(param, foundFor.getParameter(param));
        }
        final NALKey destinct = new NALKey(foundFor.getId());
        destinct.setParameters(relevant);
        return destinct;
    }

    /**
     * executes all data aggregation processing and controller logic
     * 
     * @param usecase
     * @return null if anything goes right an new usecase if there should be an
     *         redirect
     * @throws ClassNotFoundException
     */
    public static NALKey executeController(final Controller[] ctrlList, final Model configurations,
            final IDataAccessContext dac, final Map<Object, Object> parameters) {
        NALKey next = null;
        for (final Controller controller : ctrlList) {
            final EnumSet<ConfigurationType> types = controller.getObmitedConfigurations().clone();
            boolean initWParams = false;
            if (types.contains(ConfigurationType.Parameters)) {
                types.remove(ConfigurationType.Parameters);
                initWParams = true;
            }
            // filter to only requested configs
            final Model configs = JCmsHelper.getConfigurations(types, configurations);
            if (initWParams) {
                // obmit parameters if configured
                next = controller.invoke(dac, configs, parameters);
            } else {
                next = controller.invoke(dac, configs, null);
            }
            // merge altered configs back
            configurations.putAll(configs);

            if (next != null) {
                break;
            }
        }
        return next;
    }

    /**
     * These methods implements the caching of view output based on an hashing
     * alghorythm applied on the Model. or NALKey
     * 
     * @param configs
     * @param key
     * @param content
     * @param cacheTimeout
     * @return value
     */
    @SuppressWarnings("unchecked")
    public static String applyCaching(final Model configs, final NALKey key,
            final String content) {
        /**
         * caching should support two modes after an specified timeout e.g. 60
         * seconds ofter after config changes e.g. onChange
         */
        long cacheTimeout = -1;
        if (key != null && key.getParameter("cache") != null) {
            final Object keyValue = key.getParameter("cache");
            if (keyValue instanceof List) {
                cacheTimeout = new Long(((List<String>) keyValue).get(0));
            } else if (keyValue instanceof Long) {
                cacheTimeout = (Long) keyValue;
            } else if (keyValue instanceof Integer) {
                cacheTimeout = (Integer) keyValue;
            }
        }

        final StringBuilder hashKey = new StringBuilder();
        if (cacheTimeout <= -1) {
            // -1 disabled
            return null;
        } else if (cacheTimeout == 0) {
            // 0 means hash the whole configs
            // TODO [LOW] implement proper equals for the configurations, maybe
            // also some other objects from jcms
            if (configs == null) {
                return null;
            }
            for (final Entry<ConfigurationType, Configuration<?>> config : configs.entrySet()) {
                hashKey.append(config.getValue().hashCode());
            }
        } else {
            // 0> timeout in seconds, hash the NALKey
            if (key == null) {
                return null;
            }
            hashKey.append(key.hashCode());
        }
        if (content == null) { // get
            final String result;
            if (cacheTimeout > 0) {
                result = (String) XYCache.getInstance(USECASE_OUPUT_CACHE_ID).get(CACHE_REGION, hashKey.toString(),
                        cacheTimeout);
            } else {
                result = (String) XYCache.getInstance(USECASE_OUPUT_CACHE_ID).get(CACHE_REGION, hashKey.toString());
            }
            if (result != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cache object was found! Yeah. " + hashKey);
                }
            }
            return result;
        } else { // put
            XYCache.getInstance(USECASE_OUPUT_CACHE_ID).put(CACHE_REGION, hashKey.toString(), content);
            LOG.info("Cache object was stored " + hashKey);
            return null;
        }
    }
}
