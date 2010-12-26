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
package net.xy.jcms.portal.controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * transparently adapts the gap between an cached content object and its future
 * representation
 * 
 * @author xyan
 * 
 */
public class CacheContainer<CONTENT> {
    /**
     * holds the cached and retrived content
     */
    private CONTENT content;

    /**
     * holds the threaded future object
     */
    private final Future<CONTENT> future;

    /**
     * holds an optional callback
     */
    private ICallback<CONTENT> callback = null;

    /**
     * constructor by cache content
     * 
     * @param content
     */
    public CacheContainer(final CONTENT content) {
        this.content = content;
        this.future = null;
    }

    /**
     * simplyfieing constructor setting the callback
     * 
     * @param future
     * @param callback
     */
    public CacheContainer(final Future<CONTENT> future, final ICallback<CONTENT> callback) {
        this(future);
        registerCallback(callback);
    }

    /**
     * constructor by future retrieval
     * 
     * @param future
     */
    public CacheContainer(final Future<CONTENT> future) {
        this.content = null;
        this.future = future;
    }

    /**
     * registers an callback which got immidiately called if the content is
     * already present or it got called when the
     * content would be retrieved.
     * 
     * @param callback
     */
    public void registerCallback(final ICallback<CONTENT> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback can't be null.");
        }
        if (content != null) {
            // content already ready
            callback.persist(content);
        } else {
            this.callback = callback;
        }
    }

    /**
     * get the content from whereever it comes
     * 
     * @param timeout
     * @param unit
     * @return content
     * @throws RetrievalFailure
     */
    public CONTENT get(final long timeout, final TimeUnit unit) throws RetrievalFailure {
        if (content != null) {
            return content;
        } else {
            try {
                content = future.get(timeout, unit);
                if (callback != null) {
                    // content already ready
                    callback.persist(content);
                }
                return content;
            } catch (final InterruptedException e) {
                throw new RetrievalFailure("Failure by interupted.", e);
            } catch (final ExecutionException e) {
                throw new RetrievalFailure("Failure in execution.", e);
            } catch (final TimeoutException e) {
                throw new RetrievalFailure("Failure on timeout.", e);
            }
        }
    }

    /**
     * an callback to cache the content if it is retrieved
     * 
     * @author xyan
     * 
     */
    public static interface ICallback<CONTENT> {

        /**
         * method that would be called if the content is ready to be persisted
         * 
         * @param content
         */
        public void persist(CONTENT content);
    }

    /**
     * error handling container
     * 
     * @author xyan
     * 
     */
    public static class RetrievalFailure extends Exception {
        private static final long serialVersionUID = -7314262949747221468L;

        /**
         * default
         * 
         * @param description
         * @param e
         */
        public RetrievalFailure(final String description, final Throwable e) {
            super(description, e);
        }
    }
}
