package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import static org.junit.Assert.*;

@Transactional
public class PTreeNodeMonitorResourceTest extends AnyControllerTest {

    @Test
    public void getPTreeNode() throws Exception {
        _testGetJson("/api/p-tree-node-monitor/all-linked-objects");
    }

}
