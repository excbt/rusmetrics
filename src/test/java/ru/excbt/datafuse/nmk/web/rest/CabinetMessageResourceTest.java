package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.CabinetMessageType;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

@Transactional
public class CabinetMessageResourceTest extends AnyControllerTest {

    private static final Logger log = LoggerFactory.getLogger(CabinetMessageResourceTest.class);

    @Test
    public void getAllCabinetMessageRequests() throws Exception {
        String response = _testGetJson("/api/cabinet-messages");
        log.info("\n: {}", response);
    }

    @Test
    public void getAllCabinetMessageRequestsParam() throws Exception {
        String response = _testGetJson("/api/cabinet-messages",
            b -> b.param("messageType", CabinetMessageType.REQUEST.name()));
        log.info("\n: {}", response);
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
