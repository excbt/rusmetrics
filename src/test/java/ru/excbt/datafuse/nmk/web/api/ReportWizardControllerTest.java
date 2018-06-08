package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ReportTemplate;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.ReportWizardService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.report.ReportWizardParam;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Date;

import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
public class ReportWizardControllerTest extends PortalApiTest {


	private static final Logger log = LoggerFactory
			.getLogger(ReportWizardControllerTest.class);

	@Autowired
	private ReportWizardService reportWizardService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private ReportWizardController reportWizardController;

    private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        reportWizardController = new ReportWizardController(reportWizardService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(reportWizardController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}



	@Test
	public void testCommerceReportColumnSettings() throws Exception {
        mockMvcRestWrapper.restRequest("/api/reportWizard/columnSettings/commerce").testGet();
	}


	@Test
    @Transactional
	public void testCreateWizard() throws JsonProcessingException {

		///
		ReportWizardParam reportWizardParam = new ReportWizardParam();
		reportWizardParam.setReportColumnSettings(reportWizardService
				.getReportColumnSettings());
		///
		ReportTemplate reportTemplate = new ReportTemplate();
		reportTemplate.setName("Шаблон111");
		reportTemplate.setComment("Created By Wizard");
		reportTemplate.setActiveStartDate(new Date());
		reportTemplate.set_active(true);
		//
		reportWizardParam.setReportTemplate(reportTemplate);

		String jsonBody = TestUtils.objectToJson(reportWizardParam);
		String urlStr = "/api/reportWizard/commerce";

		ResultActions resultActionsAll;
		try {
			resultActionsAll = restPortalMockMvc.perform(post(urlStr)
					.contentType(MediaType.APPLICATION_JSON).content(jsonBody)
					.with(testSecurityContext())
					.accept(MediaType.APPLICATION_JSON));

			resultActionsAll.andDo(MockMvcResultHandlers.print());

			resultActionsAll.andExpect(status().isOk());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}

	}
}
