package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.NodeDirectory;
import ru.excbt.datafuse.nmk.data.service.NodeDirectoryService;

public class NodeDirectoryServiceTest extends JpaConfigTest {

	private static final Logger logger = LoggerFactory
			.getLogger(NodeDirectoryServiceTest.class);
	
	public static final long TEST_DIRECTORY_ID = 19748646;
	
	@Autowired
	private NodeDirectoryService nodeDirectoryService;

    @PersistenceUnit
    private EntityManagerFactory emf;	
	
	@Test
	public void testGetNode() {
		
		
		NodeDirectory nd = nodeDirectoryService.getRootNode(TEST_DIRECTORY_ID);
		assertNotNull(nd);
		logger.info("Testing get root Node (nodeName={})", nd.getNodeName());
		
		logger.info("Testing save Node...");
		NodeDirectory nd2 = nodeDirectoryService.save(nd);
		assertNotNull(nd2);
		
		assertEquals(nd.getChildNodes().size(), nd2.getChildNodes().size());
		
		logger.info("Testing get root Node (nodeName={})", nd.getNodeName());
		logger.info("Child Nodes {}", nd2.getChildNodes().size());
		
		nodeDirectoryService.saveWithChildren(nd2);
		
	}

	@Test
	public void testCreateSaveDelete() {
		NodeDirectory nd = NodeDirectory.newInstance("Auto Test Node " + System.currentTimeMillis());
		nd.setNodeCaption("Needed to be deleted");
		
		nd.getChildNodes().add(NodeDirectory.newInstance("child1"));
		nd.getChildNodes().add(NodeDirectory.newInstance("child2"));
		
		nodeDirectoryService.save(nd);
		nodeDirectoryService.delete(nd);
	}
	
	
}
