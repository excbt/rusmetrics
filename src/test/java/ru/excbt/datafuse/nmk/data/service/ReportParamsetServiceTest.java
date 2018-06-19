package ru.excbt.datafuse.nmk.data.service;

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
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class ReportParamsetServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetServiceTest.class);

	private static final long TEMPLATE_PARAMSET_ID = 28344056;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Autowired
	private ReportParamsetService reportParamsetService;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ReportPeriodService reportPeriodService;


    @Autowired
	private ObjectAccessService objectAccessService;

	@Test
	public void testSelectReportParamset() {
		List<ReportParamset> reportParamsetList = reportParamsetService.selectReportTypeParamsetList(
				ReportTypeKey.COMMERCE_REPORT, true, portalUserIdsService.getCurrentIds().getSubscriberId());
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
            new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
		assertNotNull(result);

		testAddUnitToParamset(result);

		reportParamsetService.moveToArchive(result.getId());
	}

	/**
	 *
	 * @param reportParamset
	 */
	private void testAddUnitToParamset(ReportParamset reportParamset) {
		List<ContObject> contObjects = objectAccessService.findContObjects(portalUserIdsService.getCurrentIds().getSubscriberId());
		assertTrue(contObjects.size() > 0);

		ContObject co = contObjects.get(0);
		ReportParamsetUnit unit = reportParamsetService.addUnitToParamset(reportParamset, co.getId());

		List<ContObjectDTO> listCO = reportParamsetService.selectParamsetContObjects(reportParamset.getId());
		assertTrue(listCO.size() > 0);

		reportParamsetService.deleteUnitFromParamset(reportParamset.getId(), co.getId());

		if (contObjects.size() > 1) {
			co = contObjects.get(1);
			unit = reportParamsetService.addUnitToParamset(reportParamset, co.getId());
		}

	}

	@Test
    @Ignore
	public void testParamsetAvailableContObjectUnits() {
		List<ReportTemplate> reportTemplates = reportTemplateService.selectSubscriberReportTemplates(
				ReportTypeKey.COMMERCE_REPORT_M_V, true, portalUserIdsService.getCurrentIds().getSubscriberId());
		assertTrue(reportTemplates.size() > 0);
		ReportTemplate rt = reportTemplates.get(0);

		logger.info("ReportTemplate ID={}", rt.getId());

		// List<ReportParamset> rpList = reportParamsetService
		// .findReportParamsetList(rt.getId());
		// assertTrue(rpList.size() > 0);

		List<ContObjectDTO> contObjects = reportParamsetService.selectParamsetAvailableContObjectUnits(-1,
				portalUserIdsService.getCurrentIds().getSubscriberId());

		assertTrue(contObjects.size() > 0);
		logger.info("Found {} Available ContObjects", contObjects.size());
	}

	@Test
	public void testParamsetUpdateUnitObjects() {

		Long[] objectIds = { 1L, 2L, 3L, 4L, 55L };

		reportParamsetService.updateUnitToParamset(TEMPLATE_PARAMSET_ID, objectIds);

	}

    @Ignore
	@Test
	public void testReportParamsetContextLaunch() throws Exception {
		List<ReportParamset> result = reportParamsetService
				.selectReportParamsetContextLaunch(SubscriberParam.builder().subscriberId(64166466L).build());
		assertNotNull(result);
		result.forEach(i -> {
			logger.info("Found ReportParamset (id={}): {}", i.getId(), i.getName());
		});
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testReportParamsetCreateDefault() throws Exception {

		Subscriber subscriber = subscriberService.selectSubscriber(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);

		logger.info("Creating ReportParamset for {}", subscriber.getId());

		List<ReportParamset> createdReportParamsets = reportParamsetService.createDefaultReportParamsets(subscriber);

		assertTrue(!createdReportParamsets.isEmpty());

		createdReportParamsets.forEach(i -> {
			logger.info("Created ReportParamset: {}. ReportType: {}", i.getId(),
					i.getReportTemplate().getReportTypeKeyname());

		});

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testReportParamsetSubscribersCreateDefault() throws Exception {

		List<Subscriber> subscribers = subscriberService.findAllSubscribers();

		subscribers.forEach(s -> {
			logger.info("Creating ReportParamset for {}", s.getId());

			List<ReportParamset> createdReportParamsets = reportParamsetService.createDefaultReportParamsets(s);

			createdReportParamsets.forEach(i -> {
				logger.info("Created ReportParamset: {}. ReportType: {}", i.getId(),
						i.getReportTemplate().getReportTypeKeyname());

			});
		});

	}

}
