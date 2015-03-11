package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.NodeDirectory;
import ru.excbt.datafuse.nmk.data.service.NodeDirectoryService;

public class NodeDirectoryServiceTest extends JpaConfigTest {

	private static final Logger logger = LoggerFactory
			.getLogger(NodeDirectoryServiceTest.class);
	
	@Autowired
	private NodeDirectoryService nodeDirectoryService;

	@Test
	public void testGetNode() {
		NodeDirectory nd = nodeDirectoryService.getRootNode(19748646);
		assertNotNull(nd);
		logger.info("Testing get root Node (nodeName={})", nd.getNodeName());
		
		logger.info("Testing save Node...");
		NodeDirectory nd2 = nodeDirectoryService.save(nd);
		assertNotNull(nd2);
		
		assertEquals(nd.getChildNodes().size(), nd2.getChildNodes().size());
		
		logger.info("Testing get root Node (nodeName={})", nd.getNodeName());
		
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
