package ru.excbt.datafuse.nmk.data.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.service.SubscriberAccessService;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.Optional;

/**
 * Created by kovtonyk on 27.06.2017.
 */
@RunWith(SpringRunner.class)
public class SubscriberAccessServiceTest extends PortalDataTest {

    private static final Logger log = LoggerFactory.getLogger(SubscriberAccessServiceTest.class);

    @Autowired
    private SubscriberAccessService subscriberAccessService;


    @Test
    public void testContZPointRevokeGrant() throws Exception {
        Optional<Long> testSubscriberId = subscriberAccessService.findAllContZPointSubscriberIds().stream().sorted().findFirst();
        Assert.assertTrue(testSubscriberId.isPresent());

        Optional<Long> contZPointId = subscriberAccessService.findContZPointIds(testSubscriberId.get()).stream().sorted().findFirst();

        log.info("Test subscriber:{} contZPointId:{}", testSubscriberId.get(), contZPointId.get());

        subscriberAccessService.revokeContZPointAccess(
            new ContZPoint().id(contZPointId.get()),
            new Subscriber().id(testSubscriberId.get()));

        subscriberAccessService.grantContZPointAccess(
            new ContZPoint().id(contZPointId.get()),
            new Subscriber().id(testSubscriberId.get()));

        subscriberAccessService.revokeContZPointAccess(
            new ContZPoint().id(contZPointId.get()),
            new Subscriber().id(testSubscriberId.get()));

    }

    @Test
    public void testContObjectRevokeGrant() throws Exception {
        Optional<Long> testSubscriberId = subscriberAccessService.findAllContObjectSubscriberIds().stream().sorted().findFirst();
        Assert.assertTrue(testSubscriberId.isPresent());

        Optional<Long> contObjectId = subscriberAccessService.findContObjectIds(testSubscriberId.get()).stream().sorted().findFirst();

        log.info("Test subscriber:{} contObjectId:{}", testSubscriberId.get(), contObjectId.get());

        subscriberAccessService.revokeContObjectAccess(
            new ContObject().id(contObjectId.get()),
            new Subscriber().id(testSubscriberId.get()));

        subscriberAccessService.grantContObjectAccess(
            new ContObject().id(contObjectId.get()),
            new Subscriber().id(testSubscriberId.get()));

        subscriberAccessService.revokeContObjectAccess(
            new ContObject().id(contObjectId.get()),
            new Subscriber().id(testSubscriberId.get()));
        subscriberAccessService.revokeContObjectAccess(new ContObject().id(contObjectId.get()));
        Assert.assertFalse(subscriberAccessService.findContObjectIds(testSubscriberId.get()).isEmpty());

    }
}
