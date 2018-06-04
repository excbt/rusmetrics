package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeVM;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class SubscrObjectTreeResourceIntTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private SubscrObjectTreeService subscrObjectTreeService;

    private SubscrObjectTreeResource subscrObjectTreeResource;

    private MockMvcRestWrapper mockMvcRestWrapper;
    @Autowired
    private ContObjectService contObjectService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrObjectTreeResource = new SubscrObjectTreeResource(subscrObjectTreeService, portalUserIdsService, contObjectService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrObjectTreeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


    @Test
    public void getSubscrObjectTree() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/{id}", 173486396).testGet();
    }

    @Test
    public void getSubscrObjectTreeList() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1").testGet();
    }

    @Test
    public void getSubscrObjectTreePage() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/page").testGet();
    }

    @Test
    public void putSubscrObjectTree() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/new")
            .requestBuilder(b -> b.param("newTreeName", "My new Name")).testPutEmpty();
    }


    @Test
    public void putSubscrObjectTreeNodeChild() throws Exception {

        String newTreeName = "My Test Node Tree";
        Optional<SubscrObjectTreeDTO> resultTree = subscrObjectTreeService
            .addSubscrObjectTree(newTreeName, null, ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId());

        SubscrObjectTreeDTO parent = resultTree.orElseThrow(() -> DBExceptionUtil.entityNotFoundException(SubscrObjectTreeDTO.class, 0));

        assertNotNull(parent.getId());

        SubscrObjectTreeVM newNode = new SubscrObjectTreeVM();
        newNode.setParentId(parent.getId());
        newNode.setObjectName("Child Node");


        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1")
            .testPut(newNode);

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/{id}", parent.getId()).testGet();
    }

    @Test
    public void putSubscrObjectTreeNodeSibling() throws Exception {

        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1;

        String newTreeName = "My Test Node Tree";
        Optional<SubscrObjectTreeDTO> resultTree = subscrObjectTreeService
            .addSubscrObjectTree(newTreeName, null, treeType, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId());

        SubscrObjectTreeDTO parent = resultTree.orElseThrow(() -> DBExceptionUtil.entityNotFoundException(SubscrObjectTreeDTO.class, 0));

        assertNotNull(parent.getId());

        SubscrObjectTreeVM newNode1 = new SubscrObjectTreeVM();
        newNode1.setParentId(parent.getId());
        newNode1.setObjectName("Child Node 1");


        Optional<SubscrObjectTreeVM> child1Result = subscrObjectTreeService.addSubscrObjectTreeNode(newNode1, treeType, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId(),"child");

        assertTrue(child1Result.isPresent());

        SubscrObjectTreeVM newNode2 = new SubscrObjectTreeVM();
        newNode2.setParentId(parent.getId());
        newNode2.setObjectName("Child Node 2");

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1")
            .requestBuilder(b -> b.param("addMode", "sibling"))
            .testPut(newNode2);

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/{id}", parent.getId()).testGet();
    }

    @Test
    public void putSubscrObjectTreeNodeUpdate() throws Exception {

        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1;

        String newTreeName = "My Test Node Tree";
        Optional<SubscrObjectTreeDTO> resultTree = subscrObjectTreeService
            .addSubscrObjectTree(newTreeName, null, treeType, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId());

        SubscrObjectTreeDTO parent = resultTree.orElseThrow(() -> DBExceptionUtil.entityNotFoundException(SubscrObjectTreeDTO.class, 0));

        assertNotNull(parent.getId());

        SubscrObjectTreeVM newNode1 = new SubscrObjectTreeVM();
        newNode1.setParentId(parent.getId());
        newNode1.setObjectName("Child Node 1");


        Optional<SubscrObjectTreeVM> child1Result = subscrObjectTreeService.addSubscrObjectTreeNode(newNode1, treeType, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId(),"child");

        SubscrObjectTreeVM editedNode = child1Result.get();
        editedNode.setObjectName("Edited Object Name");

        assertTrue(child1Result.isPresent());

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1")
            .testPut(editedNode);

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/{id}", parent.getId()).testGet();
    }

    @Test
    public void getContObjects() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/cont-objects").testGet();
    }

}
