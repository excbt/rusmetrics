//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ru.excbt.datafuse.nmk.config.jcache;


import ru.excbt.datafuse.nmk.config.jpa.NoDefaultJCacheRegionFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.spi.CachingProvider;

public class BeanClassLoaderAwareJCacheRegionFactory extends NoDefaultJCacheRegionFactory {
    private static volatile ClassLoader classLoader;

    public BeanClassLoaderAwareJCacheRegionFactory() {
    }

    protected CacheManager getCacheManager(Properties properties) {
        Objects.requireNonNull(classLoader, "Please set Spring's classloader in the setBeanClassLoader method before using this class in Hibernate");
        CachingProvider cachingProvider = this.getCachingProvider(properties);
        String cacheManagerUri = this.getProp(properties, "hibernate.javax.cache.uri");
        URI uri = this.getUri(cachingProvider, cacheManagerUri);
        CacheManager cacheManager = cachingProvider.getCacheManager(uri, classLoader);
        setBeanClassLoader((ClassLoader)null);
        return cacheManager;
    }

    private URI getUri(CachingProvider cachingProvider, String cacheManagerUri) {
        URI uri;
        if (cacheManagerUri != null) {
            try {
                uri = new URI(cacheManagerUri);
            } catch (URISyntaxException var5) {
                throw new CacheException("Couldn't create URI from " + cacheManagerUri, var5);
            }
        } else {
            uri = cachingProvider.getDefaultURI();
        }

        return uri;
    }

    public static void setBeanClassLoader(ClassLoader classLoader) {
        classLoader = classLoader;
    }
}
