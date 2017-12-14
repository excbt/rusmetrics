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
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.widget.ContEventMonitorWidgetService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService,
            PortalUserIdsMock.rmaMockUserIds(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID, TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID));

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


    public long getSubscriberId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;
    }

    /**
     *
     * @return
     */
    public long getSubscrUserId() {
        return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
    }



}
