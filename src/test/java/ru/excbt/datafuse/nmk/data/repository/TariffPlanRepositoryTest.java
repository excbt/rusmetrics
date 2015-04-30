package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class TariffPlanRepositoryTest extends JpaSupportTest {

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
