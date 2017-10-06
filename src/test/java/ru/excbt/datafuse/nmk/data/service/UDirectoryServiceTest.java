package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class UDirectoryServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryServiceTest.class);

	@Autowired
	private UDirectoryService directoryService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 *
	 */
	@Test
	public void testAvailableDirectoriesIds() {
		List<Long> ids = directoryService.selectAvailableDirectoryIds(currentSubscriberService.getSubscriberId());
		assertTrue(ids.size() > 0);
		logger.info("Available directoryIds {}", ids);
		for (long id : ids) {
			assertTrue(directoryService.checkAvailableDirectory(currentSubscriberService.getSubscriberId(), id));
		}
	}

	/**
	 *
	 */
	@Test
	public void testCreateSaveDelete() {
		UDirectoryNode nd = UDirectoryNode.newInstance("Auto Test Node " + System.currentTimeMillis());
		nd.setNodeCaption("Needed to be deleted");

		nd.getChildNodes().add(UDirectoryNode.newInstance("child1"));
		nd.getChildNodes().add(UDirectoryNode.newInstance("child2"));

		UDirectory d = new UDirectory();
		d.setDirectoryName(nd.getNodeName());
		d.setDirectoryNode(nd);

		UDirectory savedD = directoryService.save(currentSubscriberService.getSubscriberId(), d);

		checkNotNull(savedD.getId());

		directoryService.delete(currentSubscriberService.getSubscriberId(), savedD.getId());

	}

}
