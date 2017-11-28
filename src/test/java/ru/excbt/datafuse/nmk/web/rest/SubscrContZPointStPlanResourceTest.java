package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Assert;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.SubscrContZPointStPlan;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;
import ru.excbt.datafuse.nmk.data.repository.SubscrContZPointStPlanRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrStPlanRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.SubscrContZPointStPlanMapper;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
public class SubscrContZPointStPlanResourceTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrContZPointStPlanResourceTest.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;


    private SubscrContZPointStPlanService service;

    private SubscrContZPointStPlanResource resource;

    @Autowired
    private SubscrContZPointStPlanRepository repository;

    @Autowired
    private SubscrContZPointStPlanMapper mapper;


    @Autowired
    private StPlanTemplateService stPlanTemplateService;

    @Autowired
    private SubscrStPlanService subscrStPlanService;

    @Autowired
    private ObjectAccessService objectAccessService;

    @Autowired
    private SubscrStPlanRepository subscrStPlanRepository;

    @Autowired
    private SubscrContZPointStPlanRepository subscrContZPointStPlanRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        service = new SubscrContZPointStPlanService(repository, mapper);

        resource = new SubscrContZPointStPlanResource(service, portalUserIdsService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(resource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    private SubscrStPlan createSubscrStPlan() {
        SubscrStPlan stPlan = stPlanTemplateService.cloneFromTemplate("TEMP_CHART_001");
        stPlan.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        SubscrStPlan resultPlan = subscrStPlanRepository.saveAndFlush(stPlan);
        return resultPlan;
    }

    private Long findOneContZPointId() {
        List<Long> contZPointIds =  objectAccessService.findAllContZPointIds(portalUserIdsService.getCurrentIds());
        Assert.assertTrue(contZPointIds.size() > 0);
        return contZPointIds.get(0);
    }

    @Test
    @Transactional
    public void getContZPointStPlans() throws Exception {

        SubscrStPlan stPlan = createSubscrStPlan();
        log.info("Created stPlan:{}",  stPlan.toString());

        Long contZPointId = findOneContZPointId();

        SubscrContZPointStPlan contZPointStPlan = new SubscrContZPointStPlan();
        contZPointStPlan.setContZPoint(new ContZPoint().id(contZPointId));
        contZPointStPlan.setStPlanRole("PLAN");
        contZPointStPlan.setSubscrStPlan(stPlan);
        contZPointStPlan.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        SubscrContZPointStPlan savedPlan = subscrContZPointStPlanRepository.saveAndFlush(contZPointStPlan);

        Assert.assertEquals(portalUserIdsService.getCurrentIds().getSubscriberId(), savedPlan.getSubscriberId());
        Assert.assertEquals(contZPointId, savedPlan.getContZPoint().getId());

        log.info("Subscriber of stPlan: {}", savedPlan.getSubscriberId());
        log.info("ContZPoint of stPlan: {}", savedPlan.getContZPoint().getId());

        ResultActions resultActions = restPortalContObjectMockMvc.perform(get("/api/subscr/cont-zpoint-st-plans/cont-zpoints/{contZPointId}",contZPointId))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.arrayBeatifyResult(i)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(savedPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].subscriberId").value(hasItem(savedPlan.getSubscriberId().intValue())))
            .andExpect(jsonPath("$.[*].contZPointId").value(hasItem(contZPointId.intValue())))
            .andExpect(jsonPath("$.[*].stPlanRole").value(hasItem("PLAN")))
            .andExpect(jsonPath("$.[*].subscrStPlanId").value(hasItem(savedPlan.getSubscrStPlan().getId().intValue())));

    }

}
