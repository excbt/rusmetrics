/**
 *
 */
package ru.excbt.datafuse.nmk.data.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.repository.MeterPeriodSettingRepository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.02.2017
 *
 */
@RunWith(SpringRunner.class)
public class MeterPeriodSettingServiceTest extends PortalDataTest {

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
    }


	private static final Logger log = LoggerFactory.getLogger(MeterPeriodSettingServiceTest.class);

	@Autowired
	private MeterPeriodSettingService meterPeriodSettingService;

    @Autowired
	private ObjectAccessService objectAccessService;

	@Autowired
	private MeterPeriodSettingRepository meterPeriodSettingRepository;

	/**
	 *
	 * @return
	 */
	protected MeterPeriodSettingDTO createEntity() {
		return new MeterPeriodSettingDTO();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Transactional
	@Test
	public void testSave() throws Exception {
		List<Long> ids = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds());
		assertFalse(ids.isEmpty());

		MeterPeriodSettingDTO setting = createEntity();
		log.info("src: {}" ,setting);
		MeterPeriodSettingDTO result = meterPeriodSettingService.save(setting);

		log.info("result: {}" ,result);
		assertTrue(result.getId() != null);
	}
}
