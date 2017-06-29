package ru.excbt.datafuse.nmk.data.service;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

import java.util.Optional;

/**
 * Created by kovtonyk on 27.06.2017.
 */
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class SubscriberAccessServiceTest extends JpaSupportTest {

    private static final Logger log = LoggerFactory.getLogger(SubscriberAccessServiceTest.class);

    @Autowired
    private SubscriberAccessService subscriberAccessService;


    @Test
    public void testContZPointRevokeGrant() throws Exception {
        Optional<Long> testSubscriberId = subscriberAccessService.findAllContZPointSubscriberIds().stream().sorted().findFirst();
        Assert.assertTrue(testSubscriberId.isPresent());

        Optional<Long> contZPointId = subscriberAccessService.findContZPointIds(testSubscriberId.get()).stream().sorted().findFirst();

        log.info("Test subscriber:{} contZPointId:{}", testSubscriberId.get(), contZPointId.get());

        subscriberAccessService.revokeContZPointAccess(new Subscriber().id(testSubscriberId.get()), new ContZPoint().id(contZPointId.get()));

        subscriberAccessService.grantContZPointAccess(new Subscriber().id(testSubscriberId.get()), new ContZPoint().id(contZPointId.get()));

        subscriberAccessService.revokeContZPointAccess(new Subscriber().id(testSubscriberId.get()), new ContZPoint().id(contZPointId.get()));

    }

    @Test
    public void testContObjectRevokeGrant() throws Exception {
        Optional<Long> testSubscriberId = subscriberAccessService.findAllContObjectSubscriberIds().stream().sorted().findFirst();
        Assert.assertTrue(testSubscriberId.isPresent());

        Optional<Long> contObjectId = subscriberAccessService.findContObjectIds(testSubscriberId.get()).stream().sorted().findFirst();

        log.info("Test subscriber:{} contObjectId:{}", testSubscriberId.get(), contObjectId.get());

        subscriberAccessService.revokeContObjectAccess(new Subscriber().id(testSubscriberId.get()), new ContObject().id(contObjectId.get()));

        subscriberAccessService.grantContObjectAccess(new Subscriber().id(testSubscriberId.get()), new ContObject().id(contObjectId.get()));

        subscriberAccessService.revokeContObjectAccess(new Subscriber().id(testSubscriberId.get()), new ContObject().id(contObjectId.get()));
        subscriberAccessService.revokeContObjectAccess(new ContObject().id(contObjectId.get()));
        Assert.assertFalse(subscriberAccessService.findContObjectIds(testSubscriberId.get()).isEmpty());

    }
}
