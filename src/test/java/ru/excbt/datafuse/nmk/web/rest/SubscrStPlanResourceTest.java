package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrStPlanDTO;
import ru.excbt.datafuse.nmk.data.repository.SubscrStPlanRepository;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.StPlanTemplateService;
import ru.excbt.datafuse.nmk.data.service.SubscrStPlanService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.SubscrStPlanMapper;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class SubscrStPlanResourceTest extends PortalApiTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrStPlanResourceTest.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;


    @Autowired
    private SubscrStPlanService subscrStPlanService;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private StPlanTemplateService stPlanTemplateService;

    @Autowired
    private SubscrStPlanMapper subscrStPlanMapper;

    private SubscrStPlanResource subscrStPlanResource;

    @Autowired
    private SubscrStPlanRepository subscrStPlanRepository;

    private final JsonResultViewer jsonResultViewer = new JsonResultViewer((i) -> log.info("Result Json:\n {}", i));
    private final ResultHandler objectBeatifyResultHandler = jsonResultViewer.objectBeatifyResultHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrStPlanResource = new SubscrStPlanResource(subscrStPlanService, portalUserIdsService);
        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(subscrStPlanResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    @Test
    @Transactional
    public void getStPlans() throws Exception {

        SubscrStPlan stPlan = stPlanTemplateService.cloneFromTemplate("TEMP_CHART_001");
        SubscrStPlanDTO resultPlan = subscrStPlanService.saveStPlanDTO(subscrStPlanMapper.toDto(stPlan), portalUserIdsService.getCurrentIds());
        log.info("Created stPlan:{}",  resultPlan.toString());

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/st-plans"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].id").value(hasItem(resultPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].subscriberId").value(hasItem(resultPlan.getSubscriberId().intValue())))
            .andExpect(jsonPath("$.[*].stPlanTemplateKey").value(hasItem(resultPlan.getStPlanTemplateKey())));

        log.info("Result Json:\n {}", JsonResultViewer.arrayBeatifyResult(resultActions));

    }

    @Test
    @Transactional
    public void createStPlanAndSave() throws Exception {
        ResultActions resultActions = restPortalContObjectMockMvc.perform(post("/api/subscr/st-plans/new"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andDo(objectBeatifyResultHandler)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.subscriberId").value(portalUserIdsService.getCurrentIds().getSubscriberId().intValue()));

        //log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(resultActions));

    }

    @Test
    @Transactional
    public void getBlankStPlan() throws Exception {
        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/st-plans/new"))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(i)))
            .andExpect(status().isOk())
            //.andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.subscriberId").value(portalUserIdsService.getCurrentIds().getSubscriberId().intValue()));
    }

    @Test
    @Transactional
    public void saveStPlan() throws Exception {

        SubscrStPlan subscrStPlan = stPlanTemplateService.cloneFromTemplate("TEMP_CHART_001");
        subscrStPlan.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        subscrStPlan.setSpName("Test SpName");

        SubscrStPlanDTO subscrStPlanDTO = subscrStPlanMapper.toDto(subscrStPlan);
        log.info("DTO: {}", subscrStPlanDTO.toString());

        ResultActions resultActions = restPortalContObjectMockMvc.perform(put("/api/subscr/st-plans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subscrStPlanDTO)))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(i)))
            .andExpect(status().isOk())
            //.andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.subscriberId").value(portalUserIdsService.getCurrentIds().getSubscriberId().intValue()));

    }


    @Test
    @Transactional
    public void deleteStPlan() throws Exception {

        SubscrStPlan subscrStPlan = stPlanTemplateService.cloneFromTemplate("TEMP_CHART_001");
        subscrStPlan.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        SubscrStPlan savedStPlan = subscrStPlanRepository.saveAndFlush(subscrStPlan);

        ResultActions resultActions = restPortalContObjectMockMvc.perform(delete("/api/subscr/st-plans/{id}", savedStPlan.getId()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());

    }



}
