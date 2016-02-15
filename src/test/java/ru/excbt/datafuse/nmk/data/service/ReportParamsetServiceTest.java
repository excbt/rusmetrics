package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetUnit;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;

public class ReportParamsetServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetServiceTest.class);

	private static final long TEMPLATE_PARAMSET_ID = 28344056;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ReportPeriodService reportPeriodService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Test
	public void testSelectReportParamset() {
		List<ReportParamset> reportParamsetList = reportParamsetService.selectReportTypeParamsetList(
				ReportTypeKey.COMMERCE_REPORT, true, currentSubscriberService.getSubscriberId());
		assertTrue(reportParamsetList.size() > 0);
		for (ReportParamset rp : reportParamsetList) {
			logger.info("id : {}. {}", rp.getId(), rp.getReportTemplate().getReportTypeKeyname());
		}
	}

	@Test
	public void testCreateByTemplateAndArchive() {
		ReportParamset rp = new ReportParamset();
		rp.setName("Created by template id=" + TEMPLATE_PARAMSET_ID);
		rp.setActiveStartDate(new Date());
		// rp.setReportPeriod(reportPeriodService.findByKeyname(ReportPeriodKey.TODAY));
		rp.setReportPeriodKey(ReportPeriodKey.TODAY);
		ReportParamset result = reportParamsetService.createByTemplate(TEMPLATE_PARAMSET_ID, rp, null,
				currentSubscriberService.getSubscriber());
		assertNotNull(result);

		testAddUnitToParamset(result);

		reportParamsetService.moveToArchive(result.getId());
	}

	/**
	 * 
	 * @param reportParamset
	 */
	private void testAddUnitToParamset(ReportParamset reportParamset) {
		List<ContObject> contObjects = subscrContObjectService
				.selectSubscriberContObjects(currentSubscriberService.getSubscriberId());
		assertTrue(contObjects.size() > 0);

		ContObject co = contObjects.get(0);
		ReportParamsetUnit unit = reportParamsetService.addUnitToParamset(reportParamset, co.getId());

		List<ContObject> listCO = reportParamsetService.selectParamsetContObjects(reportParamset.getId());
		assertTrue(listCO.size() > 0);

		reportParamsetService.deleteUnitFromParamset(reportParamset.getId(), co.getId());

		if (contObjects.size() > 1) {
			co = contObjects.get(1);
			unit = reportParamsetService.addUnitToParamset(reportParamset, co.getId());
		}

	}

	@Test
	public void testParamsetAvailableContObjectUnits() {
		List<ReportTemplate> reportTemplates = reportTemplateService.selectSubscriberReportTemplates(
				ReportTypeKey.COMMERCE_REPORT, true, currentSubscriberService.getSubscriberId());
		assertTrue(reportTemplates.size() > 0);
		ReportTemplate rt = reportTemplates.get(0);

		logger.info("ReportTemplate ID={}", rt.getId());

		// List<ReportParamset> rpList = reportParamsetService
		// .findReportParamsetList(rt.getId());
		// assertTrue(rpList.size() > 0);

		List<ContObject> contObjects = reportParamsetService.selectParamsetAvailableContObjectUnits(-1,
				currentSubscriberService.getSubscriberId());

		assertTrue(contObjects.size() > 0);
		logger.info("Found {} Available ContObjects", contObjects.size());
	}

	@Test
	public void testParamsetUpdateUnitObjects() {

		Long[] objectIds = { 1L, 2L, 3L, 4L, 55L };

		reportParamsetService.updateUnitToParamset(TEMPLATE_PARAMSET_ID, objectIds);

	}

	@Test
	public void testReportParamsetContextLaunch() throws Exception {
		List<ReportParamset> result = reportParamsetService.selectReportParamsetContextLaunch(64166466L);
		assertNotNull(result);
		result.forEach(i -> {
			logger.info("Found ReportParamset (id={}): {}", i.getId(), i.getName());
		});
	}
}
