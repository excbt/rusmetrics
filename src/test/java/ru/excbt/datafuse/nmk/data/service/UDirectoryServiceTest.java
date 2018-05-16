package ru.excbt.datafuse.nmk.data.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class UDirectoryServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryServiceTest.class);

	@Autowired
	private UDirectoryService directoryService;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	/**
	 *
	 */
	@Test
    @Ignore
	public void testAvailableDirectoriesIds() {
		List<Long> ids = directoryService.selectAvailableDirectoryIds(portalUserIdsService.getCurrentIds().getSubscriberId());
		assertTrue(ids.size() > 0);
		logger.info("Available directoryIds {}", ids);
		for (long id : ids) {
			assertTrue(directoryService.checkAvailableDirectory(portalUserIdsService.getCurrentIds().getSubscriberId(), id));
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

		UDirectory savedD = directoryService.save(portalUserIdsService.getCurrentIds().getSubscriberId(), d);

		checkNotNull(savedD.getId());

		directoryService.delete(portalUserIdsService.getCurrentIds().getSubscriberId(), savedD.getId());

	}

}
