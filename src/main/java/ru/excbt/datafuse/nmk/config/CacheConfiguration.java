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

import javax.cache.CacheManager;
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

            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.DataSourceType.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.ReportMetaParamCategory.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.ReportMetaParamDirectory.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.ReportMetaParamSpecialType.class.getName());

            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.ReportPeriod.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.ReportSheduleType.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.ReportType.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.SessionDetailType.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.SubscrPref.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.SubscrPrefCategory.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.SubscrType.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.SystemParam.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.TariffOption.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.TimeDetailType.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.keyname.WeatherProvider.class.getName());


            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.DeviceModel.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.DeviceObject.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource.class.getName());



            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ReportMetaParamCommon.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ReportMetaParamDirectoryItem.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial.class.getName());

            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ReportParamset.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ReportParamsetUnitFilter.class.getName());

            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ContObject.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ContZPoint.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ContZPointMetadata.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ContObjectFias.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ContObjectDaData.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrContObject.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrContGroup.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrContGroupItem.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrDataSource.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrPrefValue.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrPrefObjectTreeType.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.Subscriber.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrUser.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SystemUser.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrRole.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrTypePref.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrTypeRole.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrVCookie.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.V_AuditUser.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.UserPersistentToken.class.getName());

            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrRso.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.SubscrServicePack.class.getName());

            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ContEventType.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.ContManagement.class.getName());

            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.EnergyPassport.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.EnergyPassportSection.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionEntry.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.EnergyPassportData.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.EnergyPassportDataValue.class.getName());


            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.TemperatureChart.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.TemperatureChartItem.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.WeatherForecast.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.WeatherForecastCalc.class.getName());
            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.WeatherPlace.class.getName());

            createIfNotExists(cm, ru.excbt.datafuse.nmk.data.model.Widget.class.getName());

            //cm.createCache(com.mycompany.myapp.domain.Authority.class.getName(), jcacheConfiguration);
            //cm.createCache(com.mycompany.myapp.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            //cm.createCache(com.mycompany.myapp.domain.PersistentToken.class.getName(), jcacheConfiguration);
            //cm.createCache(com.mycompany.myapp.domain.User.class.getName() + ".persistentTokens", jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
        };
    }


    private void createIfNotExists(CacheManager cacheManager, String cacheName) {
        if(cacheManager.getCache(cacheName) == null) {
            cacheManager.createCache(cacheName, jcacheConfiguration);
        }
    }


}
