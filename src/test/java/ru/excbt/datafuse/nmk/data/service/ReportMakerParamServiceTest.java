package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class ReportMakerParamServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportMakerParamServiceTest.class);

	private final static long TEST_PARAMSET_COMMERCE = 28618264;

	@Autowired
	private ReportMakerParamService reportMakerParamService;

	/**
	 *
	 */
	@Test
    @Transactional
	public void testRequredCommerce() {

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(TEST_PARAMSET_COMMERCE);

		boolean commonCheck = reportMakerParam.isAllCommonRequiredParamsExists();

		assertTrue(commonCheck);

		boolean specialCheck = reportMakerParam.isAllSpecialRequiredParamsExists();

		assertTrue(specialCheck);
	}

	@Test
    @Transactional
	public void testParamSpecialValuesMap() {

		ReportMakerParam reportMakerParam = reportMakerParamService.newReportMakerParam(TEST_PARAMSET_COMMERCE);

		Map<String, Object> valueMap = reportMakerParamService.getParamSpecialValues(reportMakerParam);
		checkNotNull(valueMap);

		for (Map.Entry<String, Object> v : valueMap.entrySet()) {
			logger.info("value key:{}, value:{}, valueClass:{}", v.getKey(), v.getValue(),
					v.getValue().getClass().getName());
		}

	}

}
