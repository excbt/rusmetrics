package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import static org.junit.Assert.*;

@Transactional
public class PTreeNodeMonitorResourceTest extends AnyControllerTest {

    @Test
    public void getPTreeNode() throws Exception {
        _testGetJson("/api/p-tree-node-monitor/all-linked-objects", b -> b.param("nodeId", "129634385"));
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
