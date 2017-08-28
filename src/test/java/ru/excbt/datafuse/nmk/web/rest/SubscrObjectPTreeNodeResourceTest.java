package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

import java.util.Arrays;

import static org.junit.Assert.*;

@Transactional
public class SubscrObjectPTreeNodeResourceTest extends AnyControllerTest {



    @Test
    public void getPTreeNode() throws Exception {

        _testGetJson("/api/p-tree-node/" + "129634385");

    }

    @Test
    public void getPTreeNodeLeveled() throws Exception {

        RequestExtraInitializer param = (b) -> b.param("childLevel", Integer.valueOf(1).toString());

        _testGetJson("/api/p-tree-node/" + "129634385", param);

    }


}
