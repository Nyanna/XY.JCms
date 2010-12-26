package net.xy.jcms.portal.controller;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import net.xy.jcms.portal.controller.CacheContainer.RetrievalFailure;

/**
 * implements an adapter to aggregate content from cache or retrieve it in an dispached thread via an threadpool
 * 
 * @author xyan
 * 
 */
public class CacheContentAdapter<CONTENT> {
    /**
     * container link
     */
    private final CacheContainer<CONTENT> link;

    /**
     * constructor first checks for cached content otherwise it starts and new task and links the future.
     * 
     * @param cacheKey
     * @param callback
     *            will be called if the content is cached
     *            or when the future content is succesfully retrieved
     * @param threadPool
     */
    public CacheContentAdapter(final String cacheKey, final ICallback<CONTENT> callback,
            final ExecutorService threadPool) {
        if (cacheKey == null || callback == null || threadPool == null) {
            throw new IllegalArgumentException("No parameter can be null.");
        }

        final CONTENT content = callback.callForCached(cacheKey);

        if (content != null) {
            link = new CacheContainer<CONTENT>(content);
        } else {
            final Callable<CONTENT> task = new Callable<CONTENT>() {
                @Override
                public CONTENT call() throws Exception {
                    return callback.callForContent();
                }
            };
            link = new CacheContainer<CONTENT>(threadPool.submit(task),
                    new net.xy.jcms.portal.controller.CacheContainer.ICallback<CONTENT>() {

                        @Override
                        public void persist(final CONTENT content) {
                            callback.persist(content, cacheKey);
                        }
                    });
        }

    }

    /**
     * retrieves content already existend from cache or it directs to the blocking future
     * 
     * @param timeout
     * @param unit
     * @return content
     * @throws RetrievalFailure
     *             in case of timeout, exception or thread interuption
     */
    public CONTENT get(final long timeout, final TimeUnit unit) throws RetrievalFailure {
        return link.get(timeout, unit);
    }

    /**
     * the interface needed to implement the callbacks
     * 
     * @author xyan
     * 
     * @param <CONTENT>
     */
    public static interface ICallback<CONTENT> {

        /**
         * method should check if the content is already cached otherwise return null
         * 
         * @param cacheKey
         * @return null if contetn not cached
         */
        public CONTENT callForCached(final String cacheKey);

        /**
         * method is called in an new thread context and should aggregate the content
         * 
         * @return content
         */
        public CONTENT callForContent() throws Exception;

        /**
         * method will be called to persist the successfull retrieved content
         * 
         * @param content
         */
        public void persist(final CONTENT content, final String cacheKey);
    }
}
