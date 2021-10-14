import java.io.File;

/**
 * Driver to interact with the Cache
 * 
 * @param <T>
 */

public class CacheDriver<T> {
    private int level;
    private Cache<T> cache1, cache2;
    private int size1, size2;
    private int c1Hits, c2Hits;
    private int c1Refs, c2Refs;
    private String mode;

    public int getC1Refs() {
        return c1Refs;
    }

    public int getC2Refs() {
        return c2Refs;
    }

    public int getLevel() { return level; }

    public Cache<T> getCache1() {
        return cache1;
    }

    public Cache<T> getCache2() {
        return cache2;
    }

    public int getC1Hits() {
        return c1Hits;
    }

    public int getC2Hits() {
        return c2Hits;
    }

    public CacheDriver(String mode, File file, int level, int maxSize1) throws IllegalArgumentException {
        if (level < 1 || level > 2 || maxSize1 < 1) {
            throw new IllegalArgumentException("Illegal Parameters");
        }
        this.level = level;

        cache1 = new Cache<T>(mode, maxSize1, file);
        c1Hits = c2Hits = c1Refs = c2Refs = 0;
    }

    public CacheDriver(String mode, File file, int level, int maxSize1, int maxSize2) throws IllegalArgumentException {
        if (level < 1 || level > 2 || maxSize1 < 1 || maxSize1 < 1) {
            throw new IllegalArgumentException("Illegal parameters");
        }
        this.level = level;
        cache1 = new Cache<T>(mode, maxSize1, file);
        cache2 = new Cache<T>(mode, maxSize2, file);
        c1Hits = c2Hits = c1Refs = c2Refs = 0;
    }

    public void add(T object) {
        cache1.addToTop(object);
        if (level == 2) {
            cache2.addToTop(object);
        }
    }

    public void flush() {
        cache1.flush();
        if (level == 2)
            cache2.flush();
    }

    /**
     * Search cache1. Move searched object to top of cache1.
     * If there are 2 levels, search cache2. Move searched item to top of cache2.
     * @param object
     * @return
     */
    public T search(T object) {
        c1Refs++;
        T output = cache1.search(object);
        if (output != null) {    // searches for specified object and brings it to top of cache
            c1Hits++;
            if (level == 2) {
                cache2.search(object);
            }
            return output;
        }
        if (level == 2) {
            c2Refs++;
            output = cache2.search(object);
            if (output != null) {
                c2Hits++;
                cache1.addToTop(output);
                return output;
            } else {
                return null;
            }
        }
        return null;
    }
}
