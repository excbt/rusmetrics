package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;

public class TariffPlanRepositoryTest extends JpaSupportTest {

	@Autowired
	private TariffPlanRepository tarifPlanRepository; 
	
	/**
	 * 
	 */
	@Test
	public void testSelectDefaultTarifPlan() {
		List<?> result = tarifPlanRepository.selectDefaultTariffPlan(0, 0);
		assertTrue(result.size() == 0);
	}

	/**
	 * 
	 */
	@Test
	public void testSelectDefaultTarifPlanNoRSO() {
		List<?> result = tarifPlanRepository.selectDefaultTariffPlan(0);
		assertTrue(result.size() == 0);
	}

	/**
	 * 
	 */
	@Test
	public void testSelectTarifPlanNoRSO() {
		List<?> result = tarifPlanRepository.selectTariffPlan(0, 0, 0);
		assertTrue(result.size() == 0);
	}	
	
}
