package net.xy.jcms;

import net.xy.jcms.shared.cache.XYCache;

import org.junit.Test;

public class CacheTest {

    @Test
    public void testLoad() throws InterruptedException {
        final XYCache cache = XYCache.getInstance("test");
        final CacheStresser[] ts = new CacheStresser[20];
        for (CacheStresser element : ts) {
            element = new CacheStresser(cache);
            element.start();
        }
        Thread.sleep(1 * 1000); // wait
        for (final CacheStresser element : ts) {
            if (element != null) {
                element.shouldStop = true;
            }
        }
    }

    public static class CacheStresser extends Thread {

        private final XYCache cache;

        public boolean shouldStop = false;

        private final int Min = 1;
        private final int Max = 60;

        public CacheStresser(final XYCache cache) {
            this.cache = cache;
        }

        @Override
        public void run() {
            if (shouldStop) {
                return;
            }
            letsWait();
            cache.put("test", "234", "2344");
            letsWait();
            cache.put("test", "234", "2344");
            letsWait();
            cache.put("test2", "4234324", "2344");
            letsWait();
            cache.put("test2", "23434", "2344");
            letsWait();
            if (cache.get("test", "23434") == null) {
                cache.put("test", "23434", "2344");
            }
            letsWait();
            if (cache.get("test", "3656456") == null) {
                cache.put("test", "3656456", "2344");
            }
            letsWait();
            if (cache.get("test2", "xcvcxv") == null) {
                cache.put("test2", "xcvcxv", "2344");
            }
            letsWait();
            if (cache.get("test", "zuz") == null) {
                cache.put("test", "zuz", "2344");
            }
            letsWait();
            if (cache.get("test", "789") == null) {
                cache.put("test", "789", "2344");
            }
            letsWait();
            if (cache.get("test2", "cvxv") == null) {
                cache.put("test2", "cvxv", "2344");
            }
            letsWait();
            if (cache.get("test", "2ew") == null) {
                cache.put("test", "2ew", "2344");
            }
            letsWait();
            if (cache.get("test2", "364ds") == null) {
                cache.put("test2", "364ds", "2344");
            }
            letsWait();
            run();
        }

        private void letsWait() {
            try {
                Thread.sleep(Min + (int) (Math.random() * (Max - Min + 1)));
            } catch (final InterruptedException e) {
            }
        }
    }

}
