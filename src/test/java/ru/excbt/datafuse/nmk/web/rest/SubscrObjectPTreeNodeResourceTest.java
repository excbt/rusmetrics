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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectPTreeNodeService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
@WithMockUser(username = "admin", password = "admin",
    roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
        "RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN", "SUBSCR_CREATE_CABINET",
        "CABINET_USER" })
@Transactional
public class SubscrObjectPTreeNodeResourceTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrObjectPTreeNodeResourceTest.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrObjectPTreeNodeResource subscrObjectPTreeNodeResource;


    @Autowired
    SubscrObjectPTreeNodeService subscrObjectPTreeNodeService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrObjectPTreeNodeResource = new SubscrObjectPTreeNodeResource(subscrObjectPTreeNodeService, portalUserIdsService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(subscrObjectPTreeNodeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }



    @Test
    public void getPTreeNode() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node/{nodeId}", 129634385))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }

    @Test
    public void getPTreeNodeChild() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node/{nodeId}", 129634394))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }

    @Test
    public void getPTreeNodeLeveled() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node/{nodeId}", 129634385)
                .param("childLevel", Integer.valueOf(1).toString()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    public void getPTreeNodeStub() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node/{nodeId}/stub", 129634385))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }

    @Test
    public void getPTreeNodeChildStub() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node/{nodeId}/stub", 129634394))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }

    @Test
    public void getPTreeNodeLeveledStub() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/p-tree-node/{nodeId}/stub", 129634385)
                .param("childLevel", Integer.valueOf(1).toString()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }


}
