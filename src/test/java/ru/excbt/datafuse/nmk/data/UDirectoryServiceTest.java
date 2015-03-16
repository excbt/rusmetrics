package ru.excbt.datafuse.nmk.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.service.UDirectoryService;

public class UDirectoryServiceTest extends JpaConfigTest {

	
	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryServiceTest.class);
	
	@Autowired
	private UDirectoryService directoryService;
	
	@Test
	public void testAvailableDirectoriesIds() {
		List<Long> ids = directoryService.selectAvailableDirectoryIds();
		assertTrue(ids.size() > 0);
		logger.info("Available directoryIds {}", ids);
		for (long id : ids) {
			assertTrue(directoryService.checkAvailableDirectory(id));
		}
	}

	@Test
	public void testCreateSaveDelete() {
		UDirectoryNode nd = UDirectoryNode.newInstance("Auto Test Node " + System.currentTimeMillis());
		nd.setNodeCaption("Needed to be deleted");
		
		nd.getChildNodes().add(UDirectoryNode.newInstance("child1"));
		nd.getChildNodes().add(UDirectoryNode.newInstance("child2"));
		
		UDirectory d = new UDirectory();
		d.setDirectoryName(nd.getNodeName());
		d.setDirectoryNode(nd);
		
		
		
		UDirectory savedD = directoryService.save(d);
		
		checkNotNull(savedD.getId());
		
		directoryService.delete(savedD.getId());
		
		//nodeDirectoryService.save(nd);
		//nodeDirectoryService.delete(nd);
	}
		
	
}
