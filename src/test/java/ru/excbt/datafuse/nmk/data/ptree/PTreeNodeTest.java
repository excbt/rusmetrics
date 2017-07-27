package ru.excbt.datafuse.nmk.data.ptree;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;

import static org.junit.Assert.*;

public class PTreeNodeTest {

    private static final Logger log = LoggerFactory.getLogger(PTreeNodeTest.class);


    @Test
    public void testJson() throws Exception {
        PTreeNode node = new PTreeNode(PTreeNodeType.ANY);
        node.setNodeName("My Root");
        node.addContObject().setNodeName("ContObject");
        String json = JsonMapperUtils.objectToJson(node, true);
        assertNotNull(json);
        log.info("\n {}", json);
    }
}
