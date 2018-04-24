package ru.excbt.datafuse.nmk.data.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.ptree.PTreeNode;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class SubscrObjectPTreeNodeServiceTest extends PortalDataTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrObjectPTreeNodeServiceTest.class);

    @Autowired
    private SubscrObjectPTreeNodeService subscrObjectPTreeNodeService;

    @Test
    @Ignore
    public void readSubscrObjectTree() throws Exception {

        PTreeNode pTreeNode = subscrObjectPTreeNodeService.readSubscrObjectTree(129634385L);

        String json = JsonMapperUtils.objectToJson(pTreeNode, true);
        assertNotNull(json);
        log.info("\n {}", json);


    }

}
