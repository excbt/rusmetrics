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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKeyV2;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PTreeNodeMonitorService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeContObjectService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplicationTest.class)
@Transactional
public class PTreeNodeMonitorResourceTest {

    private static final Logger log = LoggerFactory.getLogger(PTreeNodeMonitorResourceTest.class);

    public static final String INVALID_CONT_SERVICE_TYPE = "invalidServiceType";
    public static final String CONT_SERVICE_TYPE = ContServiceTypeKey.HW.getKeyname();
    public static final Long NODE_ID = 129634385L;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private PTreeNodeMonitorResource pTreeNodeMonitorResource;

    @Autowired
    private PTreeNodeMonitorService pTreeNodeMonitorService;
    @Autowired
    private ObjectAccessService objectAccessService;
    @Autowired
    private SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        pTreeNodeMonitorResource = new PTreeNodeMonitorResource(
            portalUserIdsService,
            pTreeNodeMonitorService,
            objectAccessService,
            subscrObjectTreeContObjectService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(pTreeNodeMonitorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    @Test
    public void getLinkedObjectsMonitor() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node-monitor/all-linked-objects").param("nodeId", "129634385"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    public void getNodeNodeColorStatus() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node-monitor/node-color-status/{nodeId}", NODE_ID))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    public void getNodeNodeColorStatus_InvalidContServiceType() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node-monitor/node-color-status/{nodeId}", NODE_ID)
                .param("contServiceType",INVALID_CONT_SERVICE_TYPE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest());

    }

    @Test
    public void getNodeNodeColorStatus_ContServiceType() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node-monitor/node-color-status/{nodeId}", NODE_ID)
                .param("contServiceType",CONT_SERVICE_TYPE))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    public void getNodeNodeColorStatus_ContServiceType1() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node-monitor/node-color-status/{nodeId}", 130091912)
                .param("contServiceType","heat"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    public void getNodeNodeColorStatusDetails_Green1() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node-monitor/node-color-status/{nodeId}/status-details/{levelColorKey}", 130091912,
                ContEventLevelColorKeyV2.GREEN.getKeyname())
                .param("contServiceType","heat"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }

    @Test
    public void getNodeNodeColorStatusDetails_Red1() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node-monitor/node-color-status/{nodeId}/status-details/{levelColorKey}", 130091912,
                ContEventLevelColorKeyV2.RED.getKeyname())
                .param("contServiceType","heat"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }


    @Test
    public void getNodeNodeColorStatusDetails_Red() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node-monitor/node-color-status/{nodeId}/status-details/{levelColorKey}", NODE_ID,
                ContEventLevelColorKeyV2.RED.getKeyname()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    public void getNodeNodeColorStatusDetails_Green() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node-monitor/node-color-status/{nodeId}/status-details/{levelColorKey}", NODE_ID,
                ContEventLevelColorKeyV2.GREEN.getKeyname()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }


}
