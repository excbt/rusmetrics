package ru.excbt.datafuse.nmk.data.service;

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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.constant.ReportConstants.ReportTypeKey;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ReportTemplateServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportTemplateServiceTest.class);

	private static final long TEST_REPORT_TEMPLATE_ID = 28181422;
	
	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

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
				.getDefaultReportTemplates(ReportTypeKey.COMMERCE_REPORT,
						true);
		assertNotNull(resultList);
		assertTrue(resultList.size() > 0);

	}

	@Test
	public void testSubscriberReports() {
		List<ReportTemplate> resultList = reportTemplateService
				.getSubscriberReportTemplates(
						currentSubscriberService.getSubscriberId(),
						ReportTypeKey.COMMERCE_REPORT, true);
		assertNotNull(resultList);
		assertTrue(resultList.size() > 0);

	}

	@Test
	public void testReportTemplateLoad() throws IOException {
		File fileJrxml = findResource("jasper/nmk_com_report_agr.jrxml");

		assertTrue(fileJrxml.exists());

		logger.info("Resource Path: {}. {}", fileJrxml.exists(),
				fileJrxml.getAbsolutePath());

		InputStream is = new FileInputStream(fileJrxml);
		try {
			byte[] fileBytes = IOUtils.toByteArray(is);	
			reportTemplateService.saveReportTemplateBody(TEST_REPORT_TEMPLATE_ID, fileBytes, fileJrxml.getName());
		} finally {
			is.close();
		}
	}


	@Test
	public void testCreateByTemplate() {
		ReportTemplate rt = new ReportTemplate();
		rt.setName("Создан по шаблону");
		rt.setActiveStartDate(new Date());
		ReportTemplate resultRT = reportTemplateService.createByTemplate(TEST_REPORT_TEMPLATE_ID, rt);
		assertNotNull(resultRT);
		//reportTemplateService.deleteOne(resultRT);
	}
	
}
