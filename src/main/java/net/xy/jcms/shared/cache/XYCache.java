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
package net.xy.jcms.shared.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Main cache implementation
 * 
 * @author Xyan
 * 
 */
public class XYCache {

    /**
     * logger
     */
    private static final Logger LOG = Logger.getLogger(XYCache.class);

    // Access part

    /**
     * instance moutn map
     */
    final static Map<String, XYCache> instanceMounts = new HashMap<String, XYCache>();

    /**
     * constructor which also starts the cache monitor
     */
    private XYCache() {
        manager.start();
    }

    /**
     * get or creates an instance identified by an id
     * 
     * @param id
     * @return
     */
    public static XYCache getInstance(final String id) {
        if (!instanceMounts.containsKey(id)) {
            synchronized (instanceMounts) {
                if (!instanceMounts.containsKey(id)) {
                    createInstance(id);
                }
            }
        }
        return instanceMounts.get(id);
    }

    /**
     * destroys all threads and caches available in this vm
     */
    public static void destroy() {
        for (final Entry<String, XYCache> entry : instanceMounts.entrySet()) {
            entry.getValue().manager.stop = true;
        }
        LOG.error("All caches are forced to shutdown");
    }

    /**
     * creates an id base instance, each instance has is own cache and manager
     * 
     * @param id
     */
    private static void createInstance(final String id) {
        LOG.info("[CREATED] Cache instance created: " + id);
        instanceMounts.put(id, new XYCache());
    }

    // Cache part

    /**
     * cache instance
     */
    private final Map<String, Map<String, CacheObj>> base = new HashMap<String, Map<String, CacheObj>>();

    /**
     * this cache manager
     */
    private final Manager manager = new Manager(base);

    /**
     * get any value of any age
     * 
     * @param region
     * @param key
     * @return
     */
    public Object get(final String region, final String key) {
        if (base.containsKey(region)) {
            if (base.get(region).containsKey(key)) {
                return base.get(region).get(key).getObj();
            }
        }
        return null;
    }

    /**
     * get an object from cache not older than maxage
     * 
     * @param region
     * @param key
     * @param maxAge
     * @return
     */
    public Object get(final String region, final String key, final int maxAge) {
        if (base.containsKey(region)) {
            if (base.get(region).containsKey(key)) {
                final long maxOld = System.nanoTime() - maxAge;
                final CacheObj obj = base.get(region).get(key);
                if (obj != null && obj.getTimeStamp() > maxOld) {
                    return obj.getObj();
                }
                LOG.info("[NULL] Object was to old: " + region + "." + key);
            }
        }
        return null;
    }

    /**
     * put an object to the cash or more precisely put send an request to the
     * manager
     * 
     * @param region
     * @param key
     * @param obj
     */
    public void put(final String region, final String key, final Object obj) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key) || obj == null) {
            throw new IllegalArgumentException("No parameter can be null or blank");
        }
        manager.push(new Request(region, key, obj));
    }

    // Manager
    /**
     * cache manager class
     * 
     * @author Xyan
     */
    protected static class Manager extends Thread {
        /**
         * request queue for the manager
         */
        private final Stack<Request> tasks = new Stack<XYCache.Request>();

        /**
         * reference to the cache object
         */
        private final Map<String, Map<String, CacheObj>> base;

        /**
         * constructor with cachw reference
         * 
         * @param base
         */
        public Manager(final Map<String, Map<String, CacheObj>> base) {
            this.base = base;
        }

        // TODO [LOW] implement flush and clean operation
        // clean auto/manual
        // auto should raised when max mem of cache get reached in incrementally
        // removes the oldes entrys firt 30 second 29, 28 until usage drops
        // below 50%

        /**
         * flag which triggers thread shutdown
         */
        protected boolean stop = false;

        @Override
        public void run() {
            if (stop) {
                return; // shutdown trigered
            }
            try {
                // LOG.debug("[SLEEP] Queue is empty sleeping a bit.");
                Thread.sleep(50);
                // LOG.debug("[WAKEUP] RingRing time to wakeup.");
            } catch (final InterruptedException e) {
                LOG.error("The thread was forced to shutdown.");
                return;
            }
            while (!tasks.isEmpty()) {
                proccessRequest(tasks.pop());
            }
            run();
        }

        /**
         * decides how to handle an request
         * 
         * @param req
         * @return
         */
        private void proccessRequest(final Request req) {
            // LOG.debug("Received request " + req.getRegion() + "." +
            // req.getKey() + " " + req.getTimestamp());
            if (!base.containsKey(req.getRegion())) {
                // create region if not exists
                // LOG.debug("[CREATED] Region initially created " +
                // req.getRegion());
                base.put(req.getRegion(), new HashMap<String, CacheObj>());
            }
            boolean isUpdate = true; // default update
            if (base.get(req.getRegion()).containsKey(req.getKey())) {
                // update only if request is newer
                isUpdate = req.getTimestamp() > base.get(req.getRegion()).get(req.getKey()).getTimeStamp();
            }
            if (isUpdate) {
                // LOG.debug("[CREATED] Request was processed and inserted " +
                // req.getRegion() + "." + req.getKey() + " "
                // + req.getTimestamp());
                base.get(req.getRegion()).put(req.getKey(), new CacheObj(req.getTimestamp(), req.getObj()));
            }
            // if (!isUpdate) {
            // LOG.debug("[DROP] Received request was to old and would be skipped"
            // + req.getRegion() + "." + req.getKey()
            // + " "
            // + req.getTimestamp());
            // }
            return;
        }

        /**
         * pushes an request to the managers qeue
         * 
         * @param req
         */
        public void push(final Request req) {
            tasks.push(req);
        }
    }

    // Transfer

    /**
     * request object of an transaction
     */
    protected static class Request {
        /**
         * timestamp on which this request was created
         */
        final long timestamp = System.nanoTime();

        /**
         * region for this request
         */
        final String region;

        /**
         * key for this request
         */
        final String key;

        /**
         * object which should be inserted
         */
        final Object obj;

        /**
         * default constructor
         * 
         * @param region
         * @param key
         * @param object
         */
        public Request(final String region, final String key, final Object object) {
            this.region = region;
            this.key = key;
            obj = object;
        }

        /**
         * get the timetsampt
         * 
         * @return
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * get the region to which this request belongs
         * 
         * @return
         */
        public String getRegion() {
            return region;
        }

        /**
         * get the key for this request
         * 
         * @return
         */
        public String getKey() {
            return key;
        }

        /**
         * get this requests object
         * 
         * @return
         */
        public Object getObj() {
            return obj;
        }
    }

    /**
     * represents an cache object
     * 
     * @author Xyan
     * 
     */
    protected static class CacheObj {
        /**
         * timetsamp of insert or last update
         */
        final long timeStamp;

        /**
         * object self
         */
        final Object obj;

        /**
         * default constructor
         * 
         * @param timeStamp
         * @param object
         */
        public CacheObj(final long timeStamp, final Object object) {
            this.timeStamp = timeStamp;
            obj = object;
        }

        /**
         * gets the timestamp
         * 
         * @return
         */
        public long getTimeStamp() {
            return timeStamp;
        }

        /**
         * get the object
         * 
         * @return
         */
        public Object getObj() {
            return obj;
        }
    }
}
