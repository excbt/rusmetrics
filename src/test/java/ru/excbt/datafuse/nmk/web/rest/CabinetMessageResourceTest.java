package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

import static org.junit.Assert.*;

@Transactional
public class CabinetMessageResourceTest extends AnyControllerTest {

    @Test
    public void getAllCabinetMessageRequests() throws Exception {
        _testGetJson("/api/cabinet-messages");
    }

    @Override
    public long getSubscriberId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
    }

    /*

     */
    @Override
    public long getSubscrUserId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
    }



}
