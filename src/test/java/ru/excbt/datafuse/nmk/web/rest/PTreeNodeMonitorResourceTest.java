package ru.excbt.datafuse.nmk.web.rest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.repository.ContEventMonitorV3Repository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
public class PTreeNodeMonitorResourceTest extends AnyControllerTest {

    private static final Logger log = LoggerFactory.getLogger(PTreeNodeMonitorResourceTest.class);

    @Autowired
    private ContEventMonitorV3Repository contEventMonitorV3Repository;

    @Test
    public void getPTreeNode() throws Exception {
        String result = _testGetJson("/api/p-tree-node-monitor/all-linked-objects", b -> b.param("nodeId", "129634385"));
        JSONArray resultJsonArray = new JSONArray(result);
        log.info("Result Json:\n {}", resultJsonArray.toString(4));
    }

    @Override
    public long getSubscriberId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
    }

    /**
     *
     * @return
     */
    @Override
    public long getSubscrUserId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
    }


    @Test
    public void testRepository() throws Exception {
        List<?> resultList = contEventMonitorV3Repository.selectCityContObjectMonitorEventCount(getSubscriberId());
        assertNotNull(resultList);
    }
}
