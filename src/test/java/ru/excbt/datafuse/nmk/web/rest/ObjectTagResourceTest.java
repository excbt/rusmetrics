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
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.ObjectTag;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagRepository;
import ru.excbt.datafuse.nmk.data.service.ObjectTagService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
public class ObjectTagResourceTest {

    private static final Logger log = LoggerFactory.getLogger(ObjectTagResourceTest.class);

    public static final int TEST_OBJECT_ID = 10;
    public static final String TEST_TAG_NAME = "my beautiful tag";

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private ObjectTagService objectTagService;

    private ObjectTagResource objectTagResource;

    @Autowired
    private ObjectTagRepository objectTagRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
        objectTagResource = new ObjectTagResource(objectTagService, portalUserIdsService);
        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(objectTagResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    private ObjectTag createObjectTag(String objectTagKeyname) {
        ObjectTag tag = new ObjectTag();
        tag.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        tag.setObjectTagKeyname(objectTagKeyname);
        return tag;
    }

    @Test
    @Transactional
    public void findContObjectsTags() throws Exception {

        String objectTagKeyname = "cont-object";

        ObjectTag tag = createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
        objectTagRepository.save(tag);

        restPortalContObjectMockMvc.perform(
            get("/api/object-tags/cont-objects"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.arrayBeatifyResult(i)))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(TEST_OBJECT_ID)))
            .andExpect(jsonPath("$.[*].objectTagKeyname").value(hasItem(objectTagKeyname)))
            .andExpect(jsonPath("$.[*].tagName").value(TEST_TAG_NAME));


    }

}
