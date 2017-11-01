package ru.excbt.datafuse.nmk.web.rest.widgets;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import static org.junit.Assert.*;

@Transactional
public class ContEventMonitorWidgetResourceTest extends AnyControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ContEventMonitorWidgetResourceTest.class);


    private void showJsonArray(String s) throws JSONException {
        JSONArray resultJsonArray = new JSONArray(s);
        log.info("Result Json:\n {}", resultJsonArray.toString(4));

    }

    @Test
    public void testGetContObjectsStats() throws Exception {
        String content = _testGetJson("/api/widgets/cont-event-monitor/p-tree-node/stats");
        assertNotNull(content);
        showJsonArray(content);
    }


    @Test
    public void testGetContObjectNodeStats() throws Exception {
        String content = _testGetJson("/api/widgets/cont-event-monitor/p-tree-node/stats", b -> b.param("nodeId", "129634385"));
        assertNotNull(content);
        showJsonArray(content);
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



}
