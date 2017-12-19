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
import ru.excbt.datafuse.nmk.data.model.dto.ObjectTagDTO;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagRepository;
import ru.excbt.datafuse.nmk.data.service.ObjectTagService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.ObjectTagMapper;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private ObjectTagMapper objectTagMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
        objectTagResource = new ObjectTagResource(objectTagService, portalUserIdsService);
        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(objectTagResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    /**
     *
     * @param objectTagKeyname
     * @return
     */
    private ObjectTag createObjectTag(String objectTagKeyname) {
        ObjectTag tag = new ObjectTag();
        tag.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        tag.setObjectTagKeyname(objectTagKeyname);
        return tag;
    }


    /**
     *
     * @param url
     * @param objectTagKeyname
     * @throws Exception
     */
    private void testFindObjectsTag(final String url, final String objectTagKeyname) throws Exception {
        ObjectTag tag = createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
        objectTagRepository.save(tag);

        restPortalContObjectMockMvc.perform(
            get(url))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.arrayBeatifyResult(i)))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(TEST_OBJECT_ID)))
            .andExpect(jsonPath("$.[*].objectTagKeyname").value(hasItem(objectTagKeyname)))
            .andExpect(jsonPath("$.[*].tagName").value(hasItem(TEST_TAG_NAME)));
    }

    /**
     *
     * @param url
     * @param objectTagKeyname
     * @throws Exception
     */
    private void testFindOneObjectTag(final String url, final String objectTagKeyname) throws Exception {
        ObjectTag tag = createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
        objectTagRepository.save(tag);

        restPortalContObjectMockMvc.perform(
            get(url, TEST_OBJECT_ID))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.arrayBeatifyResult(i)))
            .andExpect(jsonPath("$.[*].objectId").value(hasItem(TEST_OBJECT_ID)))
            .andExpect(jsonPath("$.[*].objectTagKeyname").value(hasItem(objectTagKeyname)))
            .andExpect(jsonPath("$.[*].tagName").value(hasItem(TEST_TAG_NAME)));
    }

    /**
     *
     * @throws Exception
     */
    private void _testCreatePutObjectTag(final String url, final String objectTagKeyname) throws Exception {

        ObjectTag tag = createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
        ObjectTagDTO dto = objectTagMapper.toDto(tag);

        restPortalContObjectMockMvc.perform(put(url)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(i)))
            .andExpect(jsonPath("$.objectId").value(TEST_OBJECT_ID))
            .andExpect(jsonPath("$.objectTagKeyname").value(objectTagKeyname))
            .andExpect(jsonPath("$.tagName").value(TEST_TAG_NAME));

    }

    /**
     *
     * @param url
     * @param objectTagKeyname
     * @throws Exception
     */
    private void _testCreatePostObjectTag(final String url, final String objectTagKeyname) throws Exception {

        ObjectTag tag = createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
        ObjectTagDTO dto = objectTagMapper.toDto(tag);

        restPortalContObjectMockMvc.perform(post(url)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(i)))
            .andExpect(jsonPath("$.objectId").value(TEST_OBJECT_ID))
            .andExpect(jsonPath("$.objectTagKeyname").value(objectTagKeyname))
            .andExpect(jsonPath("$.tagName").value(TEST_TAG_NAME));

    }

    /**
     *
     * @param url
     * @param objectTagKeyname
     * @throws Exception
     */
    private void _testDeleteObjectTag(final String url, final String objectTagKeyname) throws Exception {

        ObjectTag tag = createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
        ObjectTagDTO dto = objectTagMapper.toDto(tag);

        restPortalContObjectMockMvc.perform(put(url)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto))
            .param("delete", "true"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(i)));

    }


    private void _testGetObjectTagNames(final String url, final String objectTagKeyname) throws Exception {


        List<String> tagNames = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ObjectTag tag = createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME + " #" + i ).objectId(TEST_OBJECT_ID);
            tagNames.add(tag.getTagName());
            objectTagRepository.saveAndFlush(tag);
        }

        restPortalContObjectMockMvc.perform(get(url))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(i)))
            .andExpect(jsonPath("$.objectTagKeyname").value(objectTagKeyname))
            .andExpect(jsonPath("$.tagNames[*]").value(hasItem(tagNames.get(0))));
    }



    // ContObject-tag

    @Test
    @Transactional
    public void findContObjectsTags() throws Exception {
        testFindObjectsTag(
            "/api/object-tags/cont-objects",
            ObjectTag.contObjectTagKeyname);
    }


    @Test
    @Transactional
    public void findOneContObjectTags() throws Exception {
        testFindOneObjectTag(
            "/api/object-tags/cont-objects/{objectId}",
            ObjectTag.contObjectTagKeyname);
    }


    @Test
    @Transactional
    public void createPostContObjectTag() throws Exception {
        _testCreatePostObjectTag(
            "/api/object-tags/cont-objects",
            ObjectTag.contObjectTagKeyname);
    }


    @Test
    @Transactional
    public void createPutContObjectTag() throws Exception {
        _testCreatePutObjectTag(
            "/api/object-tags/cont-objects",
            ObjectTag.contObjectTagKeyname);
    }


    @Test
    @Transactional
    public void deleteContObjectTag() throws Exception {
        _testDeleteObjectTag(
            "/api/object-tags/cont-objects",
            ObjectTag.contObjectTagKeyname);
    }


    // ContZPoint - tag

    @Test
    @Transactional
    public void findContZPointTags() throws Exception {
        testFindObjectsTag(
            "/api/object-tags/cont-zpoints",
            ObjectTag.contZPointTagKeyname);
    }


    @Test
    @Transactional
    public void findOneContZPointTags() throws Exception {
        testFindOneObjectTag(
            "/api/object-tags/cont-zpoints/{objectId}",
            ObjectTag.contZPointTagKeyname);
    }


    @Test
    @Transactional
    public void createPostContZPointTag() throws Exception {
        _testCreatePostObjectTag(
            "/api/object-tags/cont-zpoints",
            ObjectTag.contZPointTagKeyname);
    }


    @Test
    @Transactional
    public void createPutContZPointTag() throws Exception {
        _testCreatePutObjectTag(
            "/api/object-tags/cont-zpoints",
            ObjectTag.contZPointTagKeyname);
    }


    @Test
    @Transactional
    public void deleteContZPointTag() throws Exception {
        _testDeleteObjectTag(
            "/api/object-tags/cont-zpoints",
            ObjectTag.contZPointTagKeyname);
    }

    // DeviceObject - tag

    @Test
    @Transactional
    public void findDeviceObjectTags() throws Exception {
        testFindObjectsTag(
            "/api/object-tags/device-objects",
            ObjectTag.deviceObjectTagKeyname);
    }

    @Test
    @Transactional
    public void findOneDeviceObjectTags() throws Exception {
        testFindOneObjectTag(
            "/api/object-tags/device-objects/{objectId}",
            ObjectTag.deviceObjectTagKeyname);
    }


    @Test
    @Transactional
    public void createPostDeviceObjectTag() throws Exception {
        _testCreatePostObjectTag(
            "/api/object-tags/device-objects",
            ObjectTag.deviceObjectTagKeyname);
    }


    @Test
    @Transactional
    public void createPutDeviceObjectTag() throws Exception {
        _testCreatePutObjectTag(
            "/api/object-tags/device-objects",
            ObjectTag.deviceObjectTagKeyname);
    }


    @Test
    @Transactional
    public void deleteDeviceObjectTag() throws Exception {
        _testDeleteObjectTag(
            "/api/object-tags/device-objects",
            ObjectTag.deviceObjectTagKeyname);
    }


    // Unsupported tag
    @Test
    @Transactional
    public void unsupportedTag() throws Exception {

        restPortalContObjectMockMvc.perform(get("/api/object-tags/unsupported-tag-123"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isBadRequest())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.objectBeatifyResult(i)));

    }

    @Test
    public void getContObjectsTagNames() throws Exception {
        _testGetObjectTagNames("/api/object-tags/cont-objects/tag-names",
            ObjectTag.contObjectTagKeyname);
    }


    @Test
    public void getContZPointsTagNames() throws Exception {
        _testGetObjectTagNames("/api/object-tags/cont-zpoints/tag-names",
            ObjectTag.contZPointTagKeyname);
    }

    @Test
    public void getDeviceObjectsTagNames() throws Exception {
        _testGetObjectTagNames("/api/object-tags/device-objects/tag-names",
            ObjectTag.deviceObjectTagKeyname);
    }


}
