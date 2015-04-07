package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;

public class TariffPlanServiceTest extends JpaSupportTest {

	private final static long TEST_RSO_ID = 25201856;
	
	@Autowired
	private TariffPlanService tariffPlanService;

	
	@Test
	public void testInitDefaultTarifPlan() {
		tariffPlanService.deleteDefaultTariffPlan(TEST_RSO_ID );
		tariffPlanService.initDefaultTariffPlan(TEST_RSO_ID);
	}

}
