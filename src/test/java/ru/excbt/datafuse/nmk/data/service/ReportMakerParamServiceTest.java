package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.ReportMakerParam;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class ReportMakerParamServiceTest extends JpaSupportTest {

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
