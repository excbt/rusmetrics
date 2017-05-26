package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

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
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class ContZPointMetadataServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(ContZPointMetadataServiceTest.class);

	@Autowired
	private ContZPointMetadataService contZPointMetadataService;

	@Test
	public void test() throws Exception {
		List<ContZPointMetadata> metadataList = contZPointMetadataService.selectNewMetadata(63031662L, false);
		assertTrue(metadataList.size() > 0);
		Map<String, List<String>> res = contZPointMetadataService.getSrcPropsDeviceMapping(metadataList);
		assertNotNull(res);
		res.forEach((x, y) -> {
			logger.info("srcProp:{}, vals: {}", x, y.toString());
		});
	}

}
