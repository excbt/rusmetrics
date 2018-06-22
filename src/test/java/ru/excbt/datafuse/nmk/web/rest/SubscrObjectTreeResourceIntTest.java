package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.util.EntityAutomation;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.service.SubscriberAccessService;
import ru.excbt.datafuse.nmk.service.dto.SubscrObjectTreeDTO;
import ru.excbt.datafuse.nmk.service.mapper.SubscrObjectTreeMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeDataVM;
import ru.excbt.datafuse.nmk.service.vm.SubscrObjectTreeVM;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private SubscrObjectTreeMapper subscrObjectTreeMapper;

    @Autowired
    private SubscriberAccessService subscriberAccessService;

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

        SubscrObjectTreeDTO parent = resultTree.orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(SubscrObjectTreeDTO.class, 0));

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

        SubscrObjectTreeDTO parent = resultTree.orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(SubscrObjectTreeDTO.class, 0));

        assertNotNull(parent.getId());

        SubscrObjectTreeVM newNode1 = new SubscrObjectTreeVM();
        newNode1.setParentId(parent.getId());
        newNode1.setObjectName("Child Node 1");


        Optional<SubscrObjectTreeVM> child1Result = subscrObjectTreeService.addSubscrObjectTreeNode(newNode1, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId(),"child");

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
        Optional<SubscrObjectTreeVM> resultTree = createTestObjectTree();

        SubscrObjectTreeVM parent = resultTree.orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(SubscrObjectTreeDTO.class, 0));

        assertNotNull(parent.getId());

        SubscrObjectTreeVM newNode1 = new SubscrObjectTreeVM();
        newNode1.setParentId(parent.getId());
        newNode1.setObjectName("Child Node 1");


        Optional<SubscrObjectTreeVM> child1Result = subscrObjectTreeService.addSubscrObjectTreeNode(newNode1, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId(),"child");

        SubscrObjectTreeVM editedNode = child1Result.get();
        editedNode.setObjectName("Edited Object Name");

        assertTrue(child1Result.isPresent());

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1")
            .testPut(editedNode);

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/{id}", parent.getId()).testGet();
    }

    private class TestTreeCreator {

        private final SubscrObjectTreeVM root;
        private final SubscrObjectTreeVM child1;

        public TestTreeCreator() {

            Optional<SubscrObjectTreeVM> resultTree = createTestObjectTree();
            assertTrue(resultTree.isPresent());
            Optional<SubscrObjectTreeVM> childNode = createChildNode(resultTree.get());
            assertTrue(childNode.isPresent());
            ContObject contObject = EntityAutomation.createContObject("New Cont Object", contObjectService, portalUserIdsService.getCurrentIds());
            subscriberAccessService.grantContObjectAccess(contObject, testSubscriber());
            SubscrObjectTreeDataVM dataVM = new SubscrObjectTreeDataVM().addIds(contObject.getId());
            subscrObjectTreeService.addContObjectsToNode(resultTree.get().getId(), childNode.get().getId(), portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId(), dataVM);

            this.root = resultTree.get();
            this.child1 = childNode.get();
        }
    }


    @Test
    public void getContObjectsAll() throws Exception {
        TestTreeCreator testTreeCreator = new TestTreeCreator();

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/cont-objects")
            .requestBuilder(b -> b
                .param("rootNodeId", testTreeCreator.root.getId().toString())
                .param("nodeId", testTreeCreator.child1.getId().toString()))
            .testGet();
    }

    @Test
    public void getContObjectsAvailable() throws Exception {

        TestTreeCreator testTreeCreator = new TestTreeCreator();

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/cont-objects")
            .requestBuilder(b -> b.param("linkFilter", "AVAILABLE")
                .param("rootNodeId", testTreeCreator.root.getId().toString())
                .param("nodeId", testTreeCreator.child1.getId().toString()))
            .testGet();
    }

    @Test
    public void getContObjectsAvailable2() throws Exception {

        TestTreeCreator testTreeCreator = new TestTreeCreator();

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/cont-objects")
            .requestBuilder(b -> b.param("linkFilter", "AVAILABLE")
                .param("rootNodeId", String.valueOf(224941031)))
            .testGet();
    }

    @Test
    public void getContObjectsLinked() throws Exception {

        TestTreeCreator testTreeCreator = new TestTreeCreator();

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/cont-objects")
            .requestBuilder(b -> b.param("linkFilter", "LINKED")
                .param("rootNodeId", testTreeCreator.root.getId().toString())
                .param("nodeId", testTreeCreator.child1.getId().toString())
            )
            .testGet();
    }

    /**
     *
     * @return
     */
    private Optional<SubscrObjectTreeVM> createTestObjectTree() {
        ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1;

        String newTreeName = "My Test Node Tree";
        Optional<SubscrObjectTreeDTO> resultTree = subscrObjectTreeService
            .addSubscrObjectTree(newTreeName, null, treeType, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId());
        return resultTree.map(subscrObjectTreeMapper::dtoToVM);
    }

    private Optional<SubscrObjectTreeVM> createChildNode(SubscrObjectTreeVM parentNode) {
        SubscrObjectTreeVM newNode1 = new SubscrObjectTreeVM();
        newNode1.setParentId(parentNode.getId());
        newNode1.setObjectName("Child Node 1 " + System.currentTimeMillis());
        Optional<SubscrObjectTreeVM> child1Result = subscrObjectTreeService.addSubscrObjectTreeNode(newNode1, portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId(),"child");
        return child1Result;
    }

    private Subscriber testSubscriber() {
        return new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId());
    }

    /**
     *
     */
    @Test
    public void testPutAdd() throws Exception {
        Optional<SubscrObjectTreeVM> resultTree = createTestObjectTree();
        assertTrue(resultTree.isPresent());
        Optional<SubscrObjectTreeVM> childNode = createChildNode(resultTree.get());
        assertTrue(childNode.isPresent());

        ContObject contObject = EntityAutomation.createContObject("New Cont Object", contObjectService, portalUserIdsService.getCurrentIds());

        subscriberAccessService.grantContObjectAccess(contObject, testSubscriber());

        SubscrObjectTreeDataVM dataVM = new SubscrObjectTreeDataVM().addIds(contObject.getId());

        mockMvcRestWrapper.restRequest("/api/subscr-object-trees/contObjectTreeType1/add-cont-objects")
            .requestBuilder(b -> b.param("rootNodeId", resultTree.get().getId().toString())
                                .param("nodeId", childNode.get().getId().toString()))
            .testPut(dataVM);

        restPortalMockMvc.perform(put("/api/subscr-object-trees/contObjectTreeType1/add-cont-objects")
            .param("rootNodeId", resultTree.get().getId().toString())
            .param("nodeId", childNode.get().getId().toString())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataVM))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());


    }


    @Test
    public void testPutRemove() throws Exception {
        Optional<SubscrObjectTreeVM> resultTree = createTestObjectTree();
        assertTrue(resultTree.isPresent());
        Optional<SubscrObjectTreeVM> childNode = createChildNode(resultTree.get());
        assertTrue(childNode.isPresent());

        ContObject contObject = EntityAutomation.createContObject("New Cont Object", contObjectService, portalUserIdsService.getCurrentIds());

        subscriberAccessService.grantContObjectAccess(contObject, testSubscriber());

        SubscrObjectTreeDataVM dataVM = new SubscrObjectTreeDataVM().addIds(contObject.getId());

        subscrObjectTreeService.addContObjectsToNode(resultTree.get().getId(), childNode.get().getId(), portalUserIdsService.getCurrentIds(), portalUserIdsService.getCurrentIds().getSubscriberId(), dataVM);

        restPortalMockMvc.perform(put("/api/subscr-object-trees/contObjectTreeType1/remove-cont-objects")
            .param("rootNodeId", resultTree.get().getId().toString())
            .param("nodeId", childNode.get().getId().toString())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dataVM))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());


    }

    @Test
    public void testSubscrObjectTreeActive() throws Exception {
        Optional<SubscrObjectTreeVM> resultTree = createTestObjectTree();
        assertTrue(resultTree.isPresent());
        Optional<SubscrObjectTreeVM> childNode = createChildNode(resultTree.get());
        assertTrue(childNode.isPresent());

        restPortalMockMvc.perform(put("/api/subscr-object-trees/contObjectTreeType1/active")
            .param("rootNodeId", resultTree.get().getId().toString())
            .param("isActive", Boolean.FALSE.toString())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful());
    }
}
