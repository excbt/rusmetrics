package ru.excbt.datafuse.nmk.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class TariffPlanRepositoryTest extends PortalDataTest {

	@Autowired
	private TariffPlanRepository tariffPlanRepository;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 *
	 */
	@Test
	public void testSelectDefaultTarifPlan() {
		List<?> result = tariffPlanRepository.selectTariffPlanList(0, 0);
		assertTrue(result.size() == 0);
	}

	/**
	 *
	 */
	@Test
	public void testSelectDefaultTarifPlanNoRSO() {
		List<?> result = tariffPlanRepository.selectTariffPlanList(0);
		assertTrue(result.size() == 0);
	}

	/**
	 *
	 */
	// @Test
	// public void testSelectTarifPlanNoRSO() {
	// List<?> result = tarifPlanRepository.selectTariffPlan(0, 0, 0);
	// assertTrue(result.size() == 0);
	// }
	//

	@Test
	public void testSelectTarifPlanNoContObjects() {
		List<?> result = tariffPlanRepository
				.selectTariffPlanNoContObjects(currentSubscriberService
						.getSubscriberId());
		assertNotNull(result);
	}

	@Test
	public void testAvailableTariffPlanContObjects() {
		List<?> result = tariffPlanRepository.selectAvailableContObjects(
				currentSubscriberService.getSubscriberId(), 0);
		assertTrue(result.size() > 0);
	}

	@Test
	public void testAvailableTariffPlanContObjects2() {
		List<?> result = tariffPlanRepository.selectAvailableContObjects(
				currentSubscriberService.getSubscriberId(), 28761612);
		assertTrue(result.size() > 0);
	}

}
