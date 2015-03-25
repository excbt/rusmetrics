package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.constant.ParamType;
import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;

public class UDirectoryParamServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(UDirectoryParamServiceTest.class);

	private final static long TEST_DIRECTORY_ID = 19748782;
	public final static long TEST_DIRECTORY_PARAM_ID = 19748790;	

	@Autowired
	private UDirectoryParamService directoryParamService;

	@Autowired
	private UDirectoryService directoryService;


	@Test
	public void testSaveDelete() {
		logger.info("TEST_DIRECTORY_ID = {}", TEST_DIRECTORY_ID);
		
		UDirectory n = directoryService.findOne(TEST_DIRECTORY_ID);

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
