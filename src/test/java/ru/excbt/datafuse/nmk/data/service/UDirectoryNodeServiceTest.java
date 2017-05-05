package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

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
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class UDirectoryNodeServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryNodeServiceTest.class);

	public final static long TEST_DIRECTORY_ID = 19748782;
	public static final long TEST_DIRECTORY_NODE_ID = 19748646;

	@Autowired
	private UDirectoryNodeService nodeDirectoryService;

    @PersistenceUnit(unitName="nmk-p")
    private EntityManagerFactory emf;

	@Test
	public void testGetAndSaveNode() {


		UDirectoryNode nd = nodeDirectoryService.getRootNode(TEST_DIRECTORY_NODE_ID);
		assertNotNull(nd);
		logger.info("Testing get root Node (nodeName={})", nd.getNodeName());

		logger.info("Testing save Node...");
		UDirectoryNode nd2 = nodeDirectoryService.save(nd);
		assertNotNull(nd2);

		assertEquals(nd.getChildNodes().size(), nd2.getChildNodes().size());

		logger.info("Testing get root Node (nodeName={})", nd.getNodeName());
		logger.info("Child Nodes {}", nd2.getChildNodes().size());

		nodeDirectoryService.saveWithChildren(nd2);

	}

	@Test
	public void testCreateSaveDelete() {
		UDirectoryNode nd = UDirectoryNode.newInstance("Auto Test Node " + System.currentTimeMillis());
		nd.setNodeCaption("Needed to be deleted");

		nd.getChildNodes().add(UDirectoryNode.newInstance("child1"));
		nd.getChildNodes().add(UDirectoryNode.newInstance("child2"));

		nodeDirectoryService.save(nd);
		nodeDirectoryService.delete(nd);
	}


}
