package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.NodeDirectory;
import ru.excbt.datafuse.nmk.data.repository.NodeDirectoryRepository;

public class NodeDirectoryRepositiryTest extends RepositoryTest {

	private static final Logger logger = LoggerFactory
			.getLogger(NodeDirectoryRepositiryTest.class);
	
	@Autowired
	private NodeDirectoryRepository directoryRepository;
	
	
	@Test
	public void testSelectAll() {
		assertNotNull(directoryRepository);
		List<NodeDirectory> ddList = directoryRepository.selectAll();
		assertNotNull(ddList);
	}
	
	@Test
	public void testSelectBySubscrOrg() {
		List<NodeDirectory> ddList = directoryRepository.selectBySubscrOrg(728);
		for (NodeDirectory d : ddList) {
			logger.info("Found node {}", d.getNodeName());
		}		
	}
	
	@Test
	public void testFindOne() {
		NodeDirectory nd = directoryRepository.findOne(19748646L);
		assertNotNull(nd);
	}

}
