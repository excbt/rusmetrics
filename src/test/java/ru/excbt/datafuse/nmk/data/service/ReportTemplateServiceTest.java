package ru.excbt.datafuse.nmk.data.service;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateBodyRepository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.report.ReportConstants;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.utils.ResourceHelper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class ReportTemplateServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportTemplateServiceTest.class);

	private static final long TEST_REPORT_TEMPLATE_ID = 28181422;

	private static final String COMM_FILE_COMPILED = "jasper/nmk_com_report.jasper";

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	private ReportTemplateBodyRepository reportTemplateBodyRepository;

	@Autowired
	private ReportMasterTemplateBodyService reportMasterTemplateBodyService;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Test
	public void testReportTemplateCreateDelete() {
		ReportTemplate rt = new ReportTemplate();
		rt.set_active(true);
		rt.setReportTypeKeyname(ReportTypeKey.COMMERCE_REPORT.getKeyname());
		rt.setName("Коммерческий отчет");
		rt.setDescription("Тест " + System.currentTimeMillis());
		rt.setSubscriber(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
		ReportTemplate result = reportTemplateService.createOne(rt);
		reportTemplateService.deleteOne(result);
	}

	@Test
	public void testDefaultCommerceReport() {
		List<ReportTemplate> resultList = reportTemplateService
				.selectDefaultReportTemplates(ReportTypeKey.COMMERCE_REPORT, true);
		assertNotNull(resultList);
		assertTrue(resultList.size() > 0);

	}

	@Test
    @Ignore
	public void testSubscriberReports() {
		List<ReportTemplate> resultList = reportTemplateService.selectSubscriberReportTemplates(
				ReportTypeKey.COMMERCE_REPORT_M_V, true, portalUserIdsService.getCurrentIds().getSubscriberId());
		assertNotNull(resultList);
		assertTrue(resultList.size() > 0);

	}

	@Test
	@Ignore
	public void testReportTemplateLoad() throws IOException {
		File fileJrxml = ResourceHelper.findResource("jasper/nmk_com_report_agr.jrxml");

		assertTrue(fileJrxml.exists());

		logger.info("Resource Path: {}. {}", fileJrxml.exists(), fileJrxml.getAbsolutePath());

		byte[] fileBytes = null;
		InputStream is = new FileInputStream(fileJrxml);
		try {
			fileBytes = IOUtils.toByteArray(is);
		} finally {
			is.close();
		}
		checkNotNull(fileBytes);
		reportTemplateService.saveReportTemplateBody(TEST_REPORT_TEMPLATE_ID, fileBytes, fileJrxml.getName());
	}

	@Test
	public void testReportTemplateLoadCompiled() throws IOException {
		File fileJasper = ResourceHelper.findResource(COMM_FILE_COMPILED);

		assertTrue(fileJasper.exists());

		logger.info("Resource Path: {}. {}", fileJasper.exists(), fileJasper.getAbsolutePath());

		byte[] fileBytes = null;
		InputStream is = new FileInputStream(fileJasper);
		try {
			fileBytes = IOUtils.toByteArray(is);
		} finally {
			is.close();
		}
		checkNotNull(fileBytes);
		reportTemplateService.saveReportTemplateBodyCompiled(TEST_REPORT_TEMPLATE_ID, fileBytes, fileJasper.getName());
	}

	@Test
	public void testCreateByTemplate() {
		ReportTemplate rt = new ReportTemplate();
		rt.setName("Создан по шаблону");
		rt.setActiveStartDate(new Date());
		ReportTemplate resultRT = reportTemplateService.createByTemplate(TEST_REPORT_TEMPLATE_ID, rt,
				new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
		assertNotNull(resultRT);
		ReportTemplate archiveRT = reportTemplateService.moveToArchive(resultRT.getId());
		assertFalse(archiveRT.is_active());
		// reportTemplateService.deleteOne(archiveRT);
	}

	@Test
	public void testReportTemplateLoadShedule() throws IOException {
		File fileJasper = ResourceHelper.findResource("jasper/nmk_com_report.jasper");
		assertNotNull(fileJasper);
		assertTrue(fileJasper.exists());
		byte[] fileBytes = null;
		InputStream is = new FileInputStream(fileJasper);
		try {
			fileBytes = IOUtils.toByteArray(is);
		} finally {
			is.close();
		}

		List<ReportShedule> reportSheduleList = reportSheduleService
				.selectReportShedule(portalUserIdsService.getCurrentIds().getSubscriberId());

		for (ReportShedule rs : reportSheduleList) {
			logger.info("Shedule Id: {}, Report Id: {}", rs.getId(), rs.getReportTemplate().getId());

			reportTemplateService.saveReportTemplateBodyCompiled(rs.getReportTemplate().getId(), fileBytes,
					fileJasper.getName());

		}

	}

	/**
	 *
	 * @throws IOException
	 */
	@Test
	public void testReportTemplateLoadActiveBody() throws IOException {
		File fileJasper = ResourceHelper.findResource("jasper/nmk_com_report.jasper");
		assertNotNull(fileJasper);
		assertTrue(fileJasper.exists());
		byte[] fileBytes = null;
		InputStream is = new FileInputStream(fileJasper);
		try {
			fileBytes = IOUtils.toByteArray(is);
		} finally {
			is.close();
		}

		List<ReportTemplate> list = reportTemplateService.selectActiveReportTemplates(ReportTypeKey.COMMERCE_REPORT);

		for (ReportTemplate rt : list) {
			logger.info("Report Id: {}", rt.getId());

			reportTemplateService.saveReportTemplateBodyCompiled(rt.getId(), fileBytes, fileJasper.getName());

		}

	}

	/**
	 *
	 */
	@Test
	public void testLoadedReportTemplateBody() {
		ReportTemplateBody reportTemplateBody = reportTemplateBodyRepository.findOne(TEST_REPORT_TEMPLATE_ID);
		assertNotNull(reportTemplateBody);
		byte[] fileBody = reportTemplateBody.getBody();
		assertNotNull(fileBody);
		logger.info("FileBody length: {}", fileBody.length);

		byte[] fileBodyCompiled = reportTemplateBody.getBodyCompiled();
		assertNotNull(fileBodyCompiled);
		logger.info("fileBodyCompiled length: {}", fileBodyCompiled.length);
	}

	@Test
	public void updateCommonEventReportTemplateTest() {
		int result = reportTemplateService.updateCommonReportTemplateBody(ReportTypeKey.EVENT_REPORT, true, true);

		if (result == 0) {
			logger.info("Common ReportTemplate for ReportTypeKey: {} IS NOT FOUND. Create new one",
					ReportTypeKey.EVENT_REPORT);
			reportTemplateService.createCommonReportTemplate(ReportTypeKey.EVENT_REPORT);
		}
	}

	@Test
	public void updateCommonConsT1ReportTemplateTest() {
		int result = reportTemplateService.updateCommonReportTemplateBody(ReportTypeKey.CONS_T1_REPORT, true, true);

		if (result == 0) {
			logger.info("Common ReportTemplate for ReportTypeKey: {} IS NOT FOUND. Create new one",
					ReportTypeKey.CONS_T1_REPORT);
			reportTemplateService.createCommonReportTemplate(ReportTypeKey.CONS_T1_REPORT);
		}
	}

	@Test
	public void updateCommonConsT2ReportTemplateTest() {
		int result = reportTemplateService.updateCommonReportTemplateBody(ReportTypeKey.CONS_T2_REPORT, true, true);

		if (result == 0) {
			logger.info("Common ReportTemplate for ReportTypeKey: {} IS NOT FOUND. Create new one",
					ReportTypeKey.CONS_T2_REPORT);
			reportTemplateService.createCommonReportTemplate(ReportTypeKey.CONS_T2_REPORT);
		}

	}

	@Test
	public void updateCommonCommerceReportTemplateTest() {
		int result = reportTemplateService.updateCommonReportTemplateBody(ReportTypeKey.COMMERCE_REPORT, true, true);

		if (result == 0) {
			logger.info("Common ReportTemplate for ReportTypeKey: {} IS NOT FOUND. Create new one",
					ReportTypeKey.COMMERCE_REPORT);
			reportTemplateService.createCommonReportTemplate(ReportTypeKey.COMMERCE_REPORT);
		}
	}

	@Test
	public void updateCommonMetrologicalReportTemplateTest() {
		int result = reportTemplateService.updateCommonReportTemplateBody(ReportTypeKey.METROLOGICAL_REPORT,
				ReportConstants.IS_ACTIVE, ReportConstants.IS_COMPILED);

		if (result == 0) {
			logger.info("Common ReportTemplate for ReportTypeKey: {} IS NOT FOUND. Create new one",
					ReportTypeKey.METROLOGICAL_REPORT);
			reportTemplateService.createCommonReportTemplate(ReportTypeKey.METROLOGICAL_REPORT);
		}
	}

	@Test
	public void createDeleteCommonEventReportTemplateTest() {
		ReportTemplate result = reportTemplateService.createCommonReportTemplate(ReportTypeKey.EVENT_REPORT);
		assertNotNull(result);

		reportTemplateService.deleteOneCommon(result);

	}

	@Test
	@Ignore
	public void updateCommerce263() {
		reportTemplateService.updateTemplateBodyFromMaster(ReportTypeKey.COMMERCE_REPORT, 28618263, true);
	}

	@Test
	public void updateCommonConsumptionReportTemplateTest() {
		int result = reportTemplateService.updateCommonReportTemplateBody(ReportTypeKey.CONSUMPTION_REPORT,
				ReportConstants.IS_ACTIVE, ReportConstants.IS_COMPILED);

		if (result == 0) {
			logger.info("Common ReportTemplate for ReportTypeKey: {} IS NOT FOUND. Create new one",
					ReportTypeKey.CONSUMPTION_REPORT);
			reportTemplateService.createCommonReportTemplate(ReportTypeKey.CONSUMPTION_REPORT);
		}
	}

	@Test
	public void updateCommonConsumptionHistoryReportTemplateTest() {
		int result = reportTemplateService.updateCommonReportTemplateBody(ReportTypeKey.CONSUMPTION_HISTORY_REPORT,
				ReportConstants.IS_ACTIVE, ReportConstants.IS_COMPILED);

		if (result == 0) {
			logger.info("Common ReportTemplate for ReportTypeKey: {} IS NOT FOUND. Create new one",
					ReportTypeKey.CONSUMPTION_HISTORY_REPORT);
			reportTemplateService.createCommonReportTemplate(ReportTypeKey.CONSUMPTION_HISTORY_REPORT);
		}
	}

}
