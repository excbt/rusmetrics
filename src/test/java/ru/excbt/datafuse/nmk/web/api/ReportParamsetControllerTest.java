package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import ru.excbt.datafuse.nmk.data.model.ReportMetaParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportParamset;
import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetParamSpecialRepository;
import ru.excbt.datafuse.nmk.data.repository.ReportParamsetRepository;
import ru.excbt.datafuse.nmk.data.service.ReportParamsetService;
import ru.excbt.datafuse.nmk.data.service.ReportTemplateService;
import ru.excbt.datafuse.nmk.data.service.ReportTypeService;
import ru.excbt.datafuse.nmk.report.ReportOutputFileType;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;
import ru.excbt.datafuse.nmk.report.ReportTypeKey;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

import javax.transaction.Transactional;


public class ReportParamsetControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetControllerTest.class);

	private static final long TEST_REPORT_TEMPLATE_ID = 28181422;
	private static final long TEMPLATE_PARAMSET_ID = 28344056;

	private final static long TEST_PARAMSET_COMMERCE = 28618264;

	@Autowired
	private ReportTemplateService reportTemplateService;

	@Autowired
	private ReportTypeService reportTypeService;

	@Autowired
	private ReportParamsetService reportParamsetService;

    @Autowired
	private ReportParamsetRepository reportParamsetRepository;

    @Autowired
    private ReportParamsetParamSpecialRepository reportParamsetParamSpecialRepository;

    /*

     */
	@Test
    @Transactional
	public void testCommerceList() throws Exception {
		_testGetJson("/api/reportParamset/commerce");
	}

	/*

	 */
	@Test
    @Transactional
	public void testCommerceGet() throws Exception {
		_testGetJson("/api/reportParamset/commerce/" + TEST_PARAMSET_COMMERCE);
	}

	/*

	 */
	// TODO make test for Update & Delete
	@Test
    @Transactional
	public void testCommerceCreateUpdateDelete() throws Exception {
		List<ReportTemplate> commTemplates = reportTemplateService
				.selectDefaultReportTemplates(ReportTypeKey.COMMERCE_REPORT, true);

		assertTrue(commTemplates.size() > 0);

		ReportTemplate rt = commTemplates.get(0);

		ReportParamset reportParamset = new ReportParamset();
		reportParamset.set_active(true);
		reportParamset.setActiveStartDate(new Date());
		reportParamset.setName("Created by REST");
		reportParamset.setOutputFileType(ReportOutputFileType.PDF);
		reportParamset.setReportPeriodKey(ReportPeriodKey.LAST_MONTH);

		List<ReportMetaParamSpecial> metaParamSpecial = reportTypeService
				.findReportMetaParamSpecialList(ReportTypeKey.COMMERCE_REPORT);

		assertTrue(metaParamSpecial.size() > 0);

		{
			ReportParamsetParamSpecial param = ReportParamsetParamSpecial.newInstance(metaParamSpecial.get(0));
			param.setReportParamset(reportParamset);
			param.setTextValue("testValue");
			assertTrue(param.isOneValueAssigned());

			reportParamset.getParamSpecialList().add(param);
		}

		RequestExtraInitializer extraInializer = new RequestExtraInitializer() {

			@Override
			public void doInit(MockHttpServletRequestBuilder builder) {
				builder.param("reportTemplateId", rt.getId().toString());

			}
		};

		String urlStr = "/api/reportParamset/commerce";

		String objString = TestUtils.objectToJson(reportParamset);
		logger.info("objString: {}", objString);

		Long createdId = _testCreateJson(urlStr, reportParamset, extraInializer);

		ReportParamset reportParamsetNew = reportParamsetService.findReportParamset(createdId);

		assertTrue(reportParamsetNew.getParamSpecialList().size() == 1);
//
//
//		reportParamsetNew.getParamSpecialList().clear();
//		{
//			ReportParamsetParamSpecial param2 = ReportParamsetParamSpecial.newInstance(metaParamSpecial.get(0));
//			param2.setReportParamset(reportParamset);
//			param2.setTextValue("testValue222");
//			assertTrue(param2.isOneValueAssigned());
//
//			reportParamsetNew.getParamSpecialList().add(param2);
//		}
//
//        reportParamsetRepository.save(reportParamsetNew);
//		//_testUpdateJson(urlStr + "/" + createdId, reportParamsetNew);
////
////        //reportParamsetRepository.flush();
//		_testDeleteJson(urlStr + "/" + createdId);
	}


	/*

	 */
	@Test
    @Transactional
	public void testUpdateUnitParamset() throws Exception {

		long[] objectIds = { 18811504L, 18811505L };

		logger.info("Array of {}", TestUtils.arrayToString(objectIds));

		ResultActions resultAction = mockMvc
				.perform(put(String.format("/api/reportParamset/%d/contObjects", TEMPLATE_PARAMSET_ID))
						.contentType(MediaType.APPLICATION_JSON).param("contObjectIds", TestUtils.arrayToString(objectIds))
						.with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultAction.andDo(MockMvcResultHandlers.print());

		resultAction.andExpect(status().is2xxSuccessful());

	}

	/*

	 */
	@Test
    @Transactional
	public void testReportParamsetContextLaunch() throws Exception {
		_testGetJson("/api/reportParamset/menu/contextLaunch");
	}

	/*

	 */
	@Test
    @Transactional
	public void testReportDirectoryItems() throws Exception {
		_testGetJson("/api/reportParamset/directoryParamItems/param_directory_mass_volume_switch");
	}

}
