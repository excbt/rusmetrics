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
import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;
import ru.excbt.datafuse.nmk.data.model.types.ParamType;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Ignore
public class UDirectoryParamServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(UDirectoryParamServiceTest.class);

	private final static long TEST_DIRECTORY_ID = 19748782;
	public final static long TEST_DIRECTORY_PARAM_ID = 19748790;

	@Autowired
	private UDirectoryParamService directoryParamService;

	@Autowired
	private UDirectoryService directoryService;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Test
    @Ignore
	public void testSaveDelete() {
		logger.info("TEST_DIRECTORY_ID = {}", TEST_DIRECTORY_ID);

        List<Long> ids = directoryService.selectAvailableDirectoryIds(portalUserIdsService.getCurrentIds().getSubscriberId());
        assertFalse(ids.isEmpty());

		UDirectory n = directoryService.findOne(portalUserIdsService.getCurrentIds().getSubscriberId(), ids.get(0));

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
