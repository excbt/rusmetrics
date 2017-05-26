package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
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

/**
 *
 * @author kovtonyk
 *
 * Comments :
 * 	 13.03.2015 - UDirectory upgrade all tests passed
 */
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class UDirectoryRepositoryTest extends JpaSupportTest {

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
