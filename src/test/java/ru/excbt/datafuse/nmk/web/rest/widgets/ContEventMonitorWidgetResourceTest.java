package ru.excbt.datafuse.nmk.web.rest.widgets;

import org.json.JSONArray;
import org.json.JSONException;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.service.util.EntityAutomation;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.widget.ContEventMonitorWidgetService;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
@WithMockUser(username = "admin", password = "admin",
    roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
        "RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN", "SUBSCR_CREATE_CABINET",
        "CABINET_USER" })
@Transactional
public class ContEventMonitorWidgetResourceTest {

    private static final Logger log = LoggerFactory.getLogger(ContEventMonitorWidgetResourceTest.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private ContEventMonitorWidgetResource contEventMonitorWidgetResource;

    @Autowired
    private ContEventMonitorWidgetService monitorWidgetService;

    @Autowired
    private SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

    @Autowired
    private ContObjectService contObjectService;

    @Autowired
    private DeviceObjectService deviceObjectService;

    @Autowired
    private ContZPointService contZPointService;

    @Autowired
    private ContZPointRepository contZPointRepository;

    @Autowired
    private ObjectAccessService objectAccessService;

    @Autowired
    private SubscriberAccessService subscriberAccessService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        contEventMonitorWidgetResource = new ContEventMonitorWidgetResource(
            portalUserIdsService,
            monitorWidgetService,
            subscrObjectTreeContObjectService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(contEventMonitorWidgetResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    private void showJsonArray(String s) throws JSONException {
        JSONArray resultJsonArray = new JSONArray(s);
        log.info("Result Json:\n {}", resultJsonArray.toString(4));

    }

    @Test
    public void testGetContObjectsStats() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/widgets/cont-event-monitor/p-tree-node/stats"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }


    @Test
    public void testGetContObjectsStatsNested() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/widgets/cont-event-monitor/p-tree-node/stats").param("nestedTypes","true"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }


    @Test
    public void testGetContObjectNodeStats() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/widgets/cont-event-monitor/p-tree-node/stats").param("nodeId","129634385"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }

    @Test
    public void testGetContObjectNodeStatsNested() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/widgets/cont-event-monitor/p-tree-node/stats")
                .param("nodeId","129634385")
                .param("nestedTypes","true"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }


    @Test
    public void testGetContEventTypes() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/widgets/cont-event-monitor/cont-event-types"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    public void testGetContEventCategories() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/widgets/cont-event-monitor/cont-event-categories"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }


    @Test
    @Transactional
    public void getContObjectMonitorState() throws Exception {

        ContObject contObject = EntityAutomation.createContObject((co) -> {
            co.setName("MyName");
            co.setFullName("My Full Name");
        }, contObjectService, portalUserIdsService.getCurrentIds());


        DeviceObject deviceObject = EntityAutomation.createDeviceObject(
            d -> {
                d.setContObjectId(contObject.getId());
                d.setNumber("12345");
            }, deviceObjectService
        );

        ContZPoint contZPoint = EntityAutomation.createContZPoint(zp -> {
            zp.setContObjectId(contObject.getId());
            zp.setDeviceObjectId(deviceObject.getId());
            zp.setContServiceTypeKeyname(ContServiceTypeKey.HEAT.keyName());
        } , contZPointService);


        subscriberAccessService.grantContZPointAccess(
            contZPoint,
            new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));

        List<ContZPoint> checkedContZPoints = contZPointRepository.findByContObjectId(contObject.getId());
        assertThat(checkedContZPoints).hasSize(1);


        Predicate<Long> checkContZPoints = objectAccessService.objectAccessUtil().checkContZPointId(portalUserIdsService.getCurrentIds());

        assertThat(checkContZPoints.test(contZPoint.getId())).isTrue();

        restPortalContObjectMockMvc.perform(
            get("/api/widgets/cont-event-monitor/cont-objects/{contObjectId}/monitor-state", contObject.getId()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(jsonPath("$.contZPointMonitorState.[*].contServiceTypeKeyname").value(hasItem(contZPoint.getContServiceTypeKeyname())))
            .andExpect(jsonPath("$.contZPointMonitorState.[*].contZPointId").value(hasItem(contZPoint.getId().intValue())));


    }
}
