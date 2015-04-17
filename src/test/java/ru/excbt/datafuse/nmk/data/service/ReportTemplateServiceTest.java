package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.model.ReportTemplateBody;
import ru.excbt.datafuse.nmk.data.repository.ReportTemplateBodyRepository;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ReportTemplateServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportTemplateServiceTest.class);

	private static final long TEST_REPORT_TEMPLATE_ID = 28181422;
	
	private static final String COMM_FILE_COMPILED = "jasper/nmk_com_report.jasper";

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	ReportTemplateBodyRepository reportTemplateBodyRepository;

	/**
	 * 
	 * @param resourcePath
	 * @return
	 */
	private static File findResource(String resourcePath) {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		URL startDir = classLoader.getResource(".");
		URL urlResource = classLoader.getResource(resourcePath);

		File f;
		if (urlResource == null) {
			f = new File(startDir.getPath() + resourcePath);
		} else {
			f = new File(urlResource.getPath());
		}

		return f;
	}

	@Test
	public void testReportTemplateCreateDelete() {
		ReportTemplate rt = new ReportTemplate();
		rt.set_active(true);
		rt.setReportTypeKey(ReportTypeKey.COMMERCE_REPORT);
		rt.setName("Коммерческий отчет");
		rt.setDescription("Тест " + System.currentTimeMillis());
		rt.setSubscriber(currentSubscriberService.getSubscriber());
		ReportTemplate result = reportTemplateService.createOne(rt);
		reportTemplateService.deleteOne(result);
	}

	@Test
	public void testDefaultCommerceReport() {
		List<ReportTemplate> resultList = reportTemplateService
				.selectDefaultReportTemplates(ReportTypeKey.COMMERCE_REPORT,
						true);
		assertNotNull(resultList);
		assertTrue(resultList.size() > 0);

	}

	@Test
	public void testSubscriberReports() {
		List<ReportTemplate> resultList = reportTemplateService
				.selectSubscriberReportTemplates(ReportTypeKey.COMMERCE_REPORT,
						true, currentSubscriberService.getSubscriberId());
		assertNotNull(resultList);
		assertTrue(resultList.size() > 0);

	}

	@Test
	@Ignore
	public void testReportTemplateLoad() throws IOException {
		File fileJrxml = findResource("jasper/nmk_com_report_agr.jrxml");

		assertTrue(fileJrxml.exists());

		logger.info("Resource Path: {}. {}", fileJrxml.exists(),
				fileJrxml.getAbsolutePath());

		byte[] fileBytes = null;
		InputStream is = new FileInputStream(fileJrxml);
		try {
			fileBytes = IOUtils.toByteArray(is);
		} finally {
			is.close();
		}
		checkNotNull(fileBytes);
		reportTemplateService.saveReportTemplateBody(TEST_REPORT_TEMPLATE_ID,
				fileBytes, fileJrxml.getName());
	}

	@Test
	public void testReportTemplateLoadCompiled() throws IOException {
		File fileJasper = findResource(COMM_FILE_COMPILED);
		
		assertTrue(fileJasper.exists());
		
		logger.info("Resource Path: {}. {}", fileJasper.exists(),
				fileJasper.getAbsolutePath());
		
		byte[] fileBytes = null;
		InputStream is = new FileInputStream(fileJasper);
		try {
			fileBytes = IOUtils.toByteArray(is);
		} finally {
			is.close();
		}
		checkNotNull(fileBytes);
		reportTemplateService.saveReportTemplateBodyCompiled(TEST_REPORT_TEMPLATE_ID,
				fileBytes, fileJasper.getName());
	}

	@Test
	public void testCreateByTemplate() {
		ReportTemplate rt = new ReportTemplate();
		rt.setName("Создан по шаблону");
		rt.setActiveStartDate(new Date());
		ReportTemplate resultRT = reportTemplateService.createByTemplate(
				TEST_REPORT_TEMPLATE_ID, rt,
				currentSubscriberService.getSubscriber());
		assertNotNull(resultRT);
		ReportTemplate archiveRT = reportTemplateService.moveToArchive(resultRT
				.getId());
		assertFalse(archiveRT.is_active());
		// reportTemplateService.deleteOne(archiveRT);
	}

	@Test
	public void testReportTemplateLoadShedule() throws IOException {
		File fileJasper = findResource("jasper/nmk_com_report_agr.jasper");
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
				.selectReportShedule(DateTime.now(),
						currentSubscriberService.getSubscriberId());

		for (ReportShedule rs : reportSheduleList) {
			reportTemplateService.saveReportTemplateBodyCompiled(rs.getReportTemplate()
					.getId(), fileBytes, fileJasper.getName());

		}

	}

	@Test
	public void testLoadedReportTemplateBody() {
		ReportTemplateBody reportTemplateBody = reportTemplateBodyRepository
				.findOne(TEST_REPORT_TEMPLATE_ID);
		assertNotNull(reportTemplateBody);
		byte[] fileBody = reportTemplateBody.getBody();
		assertNotNull(fileBody);
		logger.info("FileBody length: {}", fileBody.length);

		byte[] fileBodyCompiled = reportTemplateBody.getBodyCompiled();
		assertNotNull(fileBodyCompiled);
		logger.info("fileBodyCompiled length: {}", fileBodyCompiled.length);
	}

}
