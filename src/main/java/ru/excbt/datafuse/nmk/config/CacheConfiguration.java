package ru.excbt.datafuse.nmk.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigLocal;
import ru.excbt.datafuse.nmk.config.jpa.JpaRawConfigLocal;
import ru.excbt.datafuse.nmk.config.mvc.WebConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * Created by kovtonyk on 02.05.2017.
 */
@Configuration
@EnableCaching
@AutoConfigureBefore(value = { WebConfigurer.class, JpaConfigLocal.class, JpaRawConfigLocal.class})
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(PortalProperties portalProperties) {
        PortalProperties.Cache.Ehcache ehcache =
            portalProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(ehcache.getTimeToLiveSeconds(), TimeUnit.SECONDS)))
                .build());
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(ru.excbt.datafuse.nmk.data.model.DeviceModel.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.DeviceObject.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.keyname.ReportType.class.getName(), jcacheConfiguration);

            cm.createCache(ru.excbt.datafuse.nmk.data.model.ContObject.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.ContZPoint.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.ContObjectFias.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.ContObjectDaData.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.Subscriber.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.SubscrUser.class.getName(), jcacheConfiguration);

            cm.createCache(ru.excbt.datafuse.nmk.data.model.EnergyPassport.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.EnergyPassportSection.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionEntry.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.EnergyPassportData.class.getName(), jcacheConfiguration);
            cm.createCache(ru.excbt.datafuse.nmk.data.model.EnergyPassportDataValue.class.getName(), jcacheConfiguration);

            //cm.createCache(com.mycompany.myapp.domain.Authority.class.getName(), jcacheConfiguration);
            //cm.createCache(com.mycompany.myapp.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            //cm.createCache(com.mycompany.myapp.domain.PersistentToken.class.getName(), jcacheConfiguration);
            //cm.createCache(com.mycompany.myapp.domain.User.class.getName() + ".persistentTokens", jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
        };
    }


}
