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
package net.xy.jcms.shared.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Main cache implementation. Smart and simple without any type of
 * synchronization.
 * Read access is always possible write access gots asynchronously proccessed by
 * an management thread. At the moment in
 * memory cache only.
 * 
 * @author Xyan
 * 
 */
public class XYCache {

    /**
     * logger
     */
    private static final Logger LOG = Logger.getLogger(XYCache.class);

    /**
     * timebase multiplicator to align obmitted expiration values. the internal
     * timebase are milliseconds so an input long of 1 has to aligned with 1000
     * * 1000 to have the meaning of one second.
     */
    private static final int TIMEBASE_MULTIPLICATOR = 1000 * 1000;

    // Access part

    /**
     * instance mount map, to hold cache instances each cache has its own
     * manager.
     */
    final static Map<String, XYCache> instanceMounts = new HashMap<String, XYCache>();

    /**
     * constructor which also starts the cache manager
     */
    private XYCache() {
        manager.setDaemon(true); // before thread starts
        manager.start();
    }

    /**
     * get or creates an instance identified by an id
     * 
     * @param id
     * @return value
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
        LOG.info("All caches are forced to shutdown");
    }

    /**
     * creates an id base instance, each instance has is own cache and manager
     * 
     * @param id
     */
    synchronized private static void createInstance(final String id) {
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
     * @return value
     */
    public Object get(final String region, final String key) {
        final Map<String, CacheObj> reg = base.get(region);
        if (reg != null) {
            final CacheObj cacheObject = reg.get(key);
            if (cacheObject != null) {
                return cacheObject.getObj();
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
     *            in seconds
     * @return value
     */
    public Object get(final String region, final String key, final long maxAge) {
        final Map<String, CacheObj> reg = base.get(region);
        if (reg != null) {
            final CacheObj cacheObject = reg.get(key);
            if (cacheObject != null) {
                final long maxOld = System.currentTimeMillis() - maxAge * TIMEBASE_MULTIPLICATOR;
                if (cacheObject.getTimeStamp() > maxOld) {
                    return cacheObject.getObj();
                }
                LOG.info("[NULL] Object was to old: " + region + "." + key);
            }
        }
        return null;
    }

    /**
     * put an object to the cash or more precisely send an put request to the
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

    /**
     * flushes all regions from this cache
     */
    public void flush() {
        manager.flush = true;
    }

    /**
     * removes outdated objects from cache
     * 
     * @param maxOld
     */
    public void clean(final int maxOld) {
        manager.clean = maxOld;
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
         * constructor with cache reference
         * 
         * @param base
         */
        public Manager(final Map<String, Map<String, CacheObj>> base) {
            this.base = base;
        }

        /**
         * flag which triggers thread shutdown
         */
        protected boolean stop = false;

        /**
         * trigger for flushing
         */
        protected boolean flush = false;

        /**
         * trigern for manual cleanup
         */
        protected int clean = 0;

        @Override
        public void run() {
            while (!stop) { // shutdown trigered
                try {
                    Thread.sleep(50);
                } catch (final InterruptedException e) {
                    LOG.error("The thread was forced to shutdown.");
                    return;
                }
                while (!tasks.isEmpty()) {
                    final Request req = tasks.pop();
                    proccessRequest(req);
                    if (flush) {
                        flush = false;
                        flushAllRegions();
                    }
                    if (clean > 0) {
                        clean(clean);
                        clean = 0;
                    }
                    if (riseCleanup()) {
                        incrementalClean();
                    }
                }
            }
        }

        /**
         * checks all region and objects if they are older than maxAge
         * 
         * @param maxAge
         *            in seconds
         */
        private void clean(final int maxAge) {
            final long oldStamp = System.currentTimeMillis() - maxAge * TIMEBASE_MULTIPLICATOR;
            for (final String regionName : base.keySet()) {
                final Map<String, CacheObj> region = base.get(regionName);
                for (final String key : region.keySet()) {
                    final CacheObj obj = region.get(key);
                    if (obj.getTimeStamp() < oldStamp) {
                        // object to old, removing
                        region.remove(key);
                    }
                }
            }
        }

        /**
         * flushes all regions
         */
        private void flushAllRegions() {
            for (final Object key : base.entrySet()) {
                base.remove(key);
            }
        }

        /**
         * checks if an cleanup should be applied
         * 
         * @return value
         */
        private boolean riseCleanup() {
            // clean auto/manual
            // auto should raised when max mem of cache get reached in
            // incrementally
            // removes the oldes entrys firt 30 second 29, 28 until usage drops
            // below 50%
            return false;
        }

        /**
         * implements an incremental cleanup until threshhold is reached or
         * cache is empty
         */
        private void incrementalClean() {
            // TODO [LOW] implement incremental cache cleanup and trigger
        }

        /**
         * decides how to handle an request
         * 
         * @param req
         */
        private void proccessRequest(final Request req) {
            Map<String, CacheObj> region = base.get(req.getRegion());
            if (region == null) {
                // create region if not exists
                region = new HashMap<String, CacheObj>();
                base.put(req.getRegion(), region);
            }
            boolean isUpdate = true; // default update
            final CacheObj cacheObject = region.get(req.getKey());
            if (cacheObject != null) {
                // update only if request is newer
                // to old requests get droped
                isUpdate = req.getTimestamp() > cacheObject.getTimeStamp();
            }
            if (isUpdate) {
                region.put(req.getKey(), new CacheObj(req.getTimestamp(), req.getObj()));
            }
            return;
        }

        /**
         * pushes an request to the managers queue
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
        final long timestamp = System.currentTimeMillis();

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
         * @return value
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * get the region to which this request belongs
         * 
         * @return value
         */
        public String getRegion() {
            return region;
        }

        /**
         * get the key for this request
         * 
         * @return value
         */
        public String getKey() {
            return key;
        }

        /**
         * get this requests object
         * 
         * @return value
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
         * @return value
         */
        public long getTimeStamp() {
            return timeStamp;
        }

        /**
         * get the object
         * 
         * @return value
         */
        public Object getObj() {
            return obj;
        }
    }
}
