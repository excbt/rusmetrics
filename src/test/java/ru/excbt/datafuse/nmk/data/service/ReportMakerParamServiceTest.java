package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;

public class ReportMakerParamServiceTest extends JpaSupportTest {

	private final static long TEST_PARAMSET_COMMERCE = 28618264;

	@Autowired
	private ReportMakerParamService reportMakerParamService;

	/**
	 * 
	 */
	@Test
	public void testRequredCommerce() {

		ReportMakerParam reportMakerParam = reportMakerParamService
				.getReportMakerParam(TEST_PARAMSET_COMMERCE);

		boolean commonCheck = reportMakerParamService
				.isAllCommonRequiredParamsExists(reportMakerParam);

		assertTrue(commonCheck);

		boolean specialCheck = reportMakerParamService
				.isAllSpecialRequiredParamsExists(reportMakerParam);

		assertTrue(specialCheck);
	}

}
