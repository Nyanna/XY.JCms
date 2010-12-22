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
    // TODO [HIGH] refactor to cachecontainer where possible
    /**
     * holds the cached and retrived content
     */
    private final CONTENT content;

    /**
     * holds the threaded future object
     */
    private final Future<CONTENT> future;

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
     * constructor by future retrieval
     * 
     * @param future
     */
    public CacheContainer(final Future<CONTENT> future) {
        this.content = null;
        this.future = future;
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
                return future.get(timeout, unit);
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
