package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class TariffPlanServiceTest extends JpaSupportTest {

	private final static long TEST_RSO_ID = 25201856;

	@Autowired
	private TariffPlanService tariffPlanService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	@Ignore
	public void testInitDefaultTarifPlan() {
		// tariffPlanService.deleteDefaultTariffPlan(TEST_RSO_ID );
		tariffPlanService.initDefaultTariffPlan(TEST_RSO_ID);
	}

	@Test
	//@Ignore
	public void testUpdateTarifPlan() {

		List<TariffPlan> tpList = tariffPlanService.selectTariffPlanList();

		assertTrue(tpList.size() > 0);

		TariffPlan tp = tpList.get(0);

		List<ContObject> contObjects = tariffPlanService
				.selectTariffPlanAvailableContObjects(tp.getId(),
						currentSubscriberService.getSubscriberId());

		assertTrue(contObjects.size() > 0);

		tp.getContObjects().add(contObjects.get(0));

		TariffPlan result = tariffPlanService.updateOne(tp);
		assertNotNull(result);
		// tariffPlanService.deleteDefaultTariffPlan(TEST_RSO_ID );
		// tariffPlanService.initDefaultTariffPlan(TEST_RSO_ID);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testUpdateTarifPlanIllgal() {

		List<TariffPlan> tpList = tariffPlanService.selectTariffPlanList();
		assertTrue(tpList.size() > 0);
		TariffPlan tp = tpList.get(0);
		tp.setEndDate(DateTime.now().minusYears(10).toDate());
		tariffPlanService.updateOne(tp);
	}

}
