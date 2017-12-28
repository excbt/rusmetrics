package ru.excbt.datafuse.nmk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.cache.CacheManager;
import java.util.ArrayList;
import java.util.List;

//@Service
public class CacheService {

    private final CacheManager cacheManager;

    @Autowired(required = false)
    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public boolean clearAllCache() {
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
        }
        return cacheManager != null;
    }


    public boolean isCacheEnabled() {
        return cacheManager != null;
    }

    public List<String> getCacheNames() {
        List<String> resultList = new ArrayList<>();
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(name -> resultList.add(name));
        }
        return resultList;

    }

}
