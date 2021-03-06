package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

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
import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;
import ru.excbt.datafuse.nmk.data.model.types.ParamType;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class UDirectoryParamServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryParamServiceTest.class);

	private final static long TEST_DIRECTORY_ID = 19748782;
	public final static long TEST_DIRECTORY_PARAM_ID = 19748790;

	@Autowired
	private UDirectoryParamService directoryParamService;

	@Autowired
	private UDirectoryService directoryService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testSaveDelete() {
		logger.info("TEST_DIRECTORY_ID = {}", TEST_DIRECTORY_ID);

		UDirectory n = directoryService.findOne(currentSubscriberService.getSubscriberId(), TEST_DIRECTORY_ID);

		assertNotNull(n);

		UDirectoryParam p = new UDirectoryParam();
		p.setParamType(ParamType.STRING.name());
		p.setParamName("Name1");
		p.setDirectory(n);

		UDirectoryParam savedP = directoryParamService.save(p);
		directoryParamService.delete(savedP);

	}

	@Test
	public void testSelectParams() {
		List<?> lst = directoryParamService.selectDirectoryParams(TEST_DIRECTORY_ID);
		assertNotNull(lst);
	}

	@Test
	public void testUpdate() {
		UDirectoryParam param = directoryParamService.findOne(TEST_DIRECTORY_PARAM_ID);
		param.setParamName("TEST Param - Name " + System.currentTimeMillis());
		directoryParamService.save(param);
	}

}
