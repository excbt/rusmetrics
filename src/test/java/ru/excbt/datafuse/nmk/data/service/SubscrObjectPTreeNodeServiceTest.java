package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.ptree.PTreeNode;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

import static org.junit.Assert.*;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class SubscrObjectPTreeNodeServiceTest extends JpaSupportTest implements SecuredRoles {

    private static final Logger log = LoggerFactory.getLogger(SubscrObjectPTreeNodeServiceTest.class);

    @Autowired
    private SubscrObjectPTreeNodeService subscrObjectPTreeNodeService;

    @Test
    public void readSubscrObjectTree() throws Exception {

        PTreeNode pTreeNode = subscrObjectPTreeNodeService.readSubscrObjectTree(129634385L);

        String json = JsonMapperUtils.objectToJson(pTreeNode, true);
        assertNotNull(json);
        log.info("\n {}", json);


    }

}
