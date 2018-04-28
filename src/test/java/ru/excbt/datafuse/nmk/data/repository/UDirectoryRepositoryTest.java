package ru.excbt.datafuse.nmk.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author kovtonyk
 *
 * Comments :
 * 	 13.03.2015 - UDirectory upgrade all tests passed
 */

@RunWith(SpringRunner.class)
public class UDirectoryRepositoryTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryRepositoryTest.class);


	public final static long TEST_SUBSCR_ORG_ID = 728;
	public final static long TEST_DIRECTORY_ID = 19748782;

	@Autowired
	private UDirectoryRepository directoryRepository;

	@Test
	public void testSelectBySubscrOrg() {
		List<UDirectory> ddList = directoryRepository.selectBySubscriber(TEST_SUBSCR_ORG_ID);
		assertTrue(ddList.size() > 0);
		for (UDirectory d : ddList) {
			logger.info("Found root node {}", d.getDirectoryName());
		}
	}

	@Test
	public void testFindOne() {
		UDirectory nd = directoryRepository.findOne(TEST_DIRECTORY_ID);
		assertNotNull(nd);
	}

}
