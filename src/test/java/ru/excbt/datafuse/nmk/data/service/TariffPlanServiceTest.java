package ru.excbt.datafuse.nmk.data.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class TariffPlanServiceTest extends PortalDataTest {

	private final static long TEST_RSO_ID = 25201856;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Autowired
	private TariffPlanService tariffPlanService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testInitDefaultTarifPlan() throws Exception {
		// tariffPlanService.deleteDefaultTariffPlan(TEST_RSO_ID );
		tariffPlanService.initDefaultTariffPlan(portalUserIdsService.getCurrentIds().getSubscriberId(), TEST_RSO_ID);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	// @Ignore
	public void testUpdateTarifPlan() throws Exception {

		List<TariffPlan> tpList = tariffPlanService.selectTariffPlanList(portalUserIdsService.getCurrentIds().getSubscriberId());

		assertTrue(tpList.size() > 0);

		TariffPlan tp = tpList.get(0);

		List<ContObject> contObjects = tariffPlanService.selectTariffPlanAvailableContObjects(tp.getId(),
				portalUserIdsService.getCurrentIds().getSubscriberId());

		if (contObjects.size() == 0) {
			return;
		}

		assertTrue(contObjects.size() > 0);

		tp.getContObjects().add(contObjects.get(0));

		TariffPlan result = tariffPlanService.updateOne(portalUserIdsService.getCurrentIds(), tp);
		assertNotNull(result);
		// tariffPlanService.deleteDefaultTariffPlan(TEST_RSO_ID );
		// tariffPlanService.initDefaultTariffPlan(TEST_RSO_ID);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateTarifPlanIllgal() throws Exception {

		List<TariffPlan> tpList = tariffPlanService.selectTariffPlanList(portalUserIdsService.getCurrentIds().getSubscriberId());
		assertTrue(tpList.size() > 0);
		TariffPlan tp = tpList.get(0);
		tp.setEndDate(DateTime.now().minusYears(10).toDate());
		tariffPlanService.updateOne(portalUserIdsService.getCurrentIds(), tp);
	}

}
