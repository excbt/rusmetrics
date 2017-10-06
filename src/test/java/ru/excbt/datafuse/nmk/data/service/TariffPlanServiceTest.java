package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class TariffPlanServiceTest extends JpaSupportTest {

	private final static long TEST_RSO_ID = 25201856;

	@Autowired
	private TariffPlanService tariffPlanService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testInitDefaultTarifPlan() throws Exception {
		// tariffPlanService.deleteDefaultTariffPlan(TEST_RSO_ID );
		tariffPlanService.initDefaultTariffPlan(currentSubscriberService.getSubscriberId(), TEST_RSO_ID);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	// @Ignore
	public void testUpdateTarifPlan() throws Exception {

		List<TariffPlan> tpList = tariffPlanService.selectTariffPlanList(currentSubscriberService.getSubscriberId());

		assertTrue(tpList.size() > 0);

		TariffPlan tp = tpList.get(0);

		List<ContObject> contObjects = tariffPlanService.selectTariffPlanAvailableContObjects(tp.getId(),
				currentSubscriberService.getSubscriberId());

		if (contObjects.size() == 0) {
			return;
		}

		assertTrue(contObjects.size() > 0);

		tp.getContObjects().add(contObjects.get(0));

		TariffPlan result = tariffPlanService.updateOne(getSubscriberParam(), tp);
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

		List<TariffPlan> tpList = tariffPlanService.selectTariffPlanList(currentSubscriberService.getSubscriberId());
		assertTrue(tpList.size() > 0);
		TariffPlan tp = tpList.get(0);
		tp.setEndDate(DateTime.now().minusYears(10).toDate());
		tariffPlanService.updateOne(getSubscriberParam(), tp);
	}

}
