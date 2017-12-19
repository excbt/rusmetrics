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
import ru.excbt.datafuse.nmk.data.model.ObjectTagInfo;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagInfoRepository;
import ru.excbt.datafuse.nmk.service.dto.ObjectTagDTO;
import ru.excbt.datafuse.nmk.data.repository.ObjectTagRepository;
import ru.excbt.datafuse.nmk.service.ObjectTagService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.dto.ObjectTagInfoDTO;
import ru.excbt.datafuse.nmk.service.mapper.ObjectTagInfoMapper;
import ru.excbt.datafuse.nmk.service.mapper.ObjectTagMapper;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ObjectTagInfoRepository objectTagInfoRepository;

    @Autowired
    private ObjectTagMapper objectTagMapper;

    @Autowired
    private ObjectTagInfoMapper objectTagInfoMapper;

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
    private ObjectTag _createObjectTag(String objectTagKeyname) {
        ObjectTag tag = new ObjectTag();
        tag.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        tag.setObjectTagKeyname(objectTagKeyname);
        return tag;
    }

    private ObjectTagInfo _createObjectTagInfo(String objectTagKeyname) {
        ObjectTagInfo tagInfo = new ObjectTagInfo();
        tagInfo.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        tagInfo.setObjectTagKeyname(objectTagKeyname);
        return tagInfo;
    }


    /**
     *
     * @param url
     * @param objectTagKeyname
     * @throws Exception
     */
    private void _testFindObjectsTag(final String url, final String objectTagKeyname) throws Exception {
        ObjectTag tag = _createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
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
    private void _testFindOneObjectTag(final String url, final String objectTagKeyname) throws Exception {
        ObjectTag tag = _createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
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

        ObjectTag tag = _createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
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

        ObjectTag tag = _createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
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

        ObjectTag tag = _createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME).objectId(TEST_OBJECT_ID);
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
            ObjectTag tag = _createObjectTag(objectTagKeyname).tagName(TEST_TAG_NAME + " #" + i ).objectId(TEST_OBJECT_ID);
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
        _testFindObjectsTag(
            "/api/object-tags/cont-objects",
            ObjectTag.contObjectTagKeyname);
    }


    @Test
    @Transactional
    public void findOneContObjectTags() throws Exception {
        _testFindOneObjectTag(
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
        _testFindObjectsTag(
            "/api/object-tags/cont-zpoints",
            ObjectTag.contZPointTagKeyname);
    }


    @Test
    @Transactional
    public void findOneContZPointTags() throws Exception {
        _testFindOneObjectTag(
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
        _testFindObjectsTag(
            "/api/object-tags/device-objects",
            ObjectTag.deviceObjectTagKeyname);
    }

    @Test
    @Transactional
    public void findOneDeviceObjectTags() throws Exception {
        _testFindOneObjectTag(
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
    @Transactional
    public void getContObjectsTagNames() throws Exception {
        _testGetObjectTagNames("/api/object-tags/cont-objects/tag-names",
            ObjectTag.contObjectTagKeyname);
    }


    @Test
    @Transactional
    public void getContZPointsTagNames() throws Exception {
        _testGetObjectTagNames("/api/object-tags/cont-zpoints/tag-names",
            ObjectTag.contZPointTagKeyname);
    }

    @Test
    @Transactional
    public void getDeviceObjectsTagNames() throws Exception {
        _testGetObjectTagNames("/api/object-tags/device-objects/tag-names",
            ObjectTag.deviceObjectTagKeyname);
    }


    @Test
    @Transactional
    public void getContObjectsTagInfo() throws Exception {

        ObjectTagInfo tagInfo = _createObjectTagInfo(ObjectTag.contObjectTagKeyname).tagName(TEST_TAG_NAME).tagColor("GREEN");
        objectTagInfoRepository.saveAndFlush(tagInfo);

        ObjectTagInfo tagInfo2 = _createObjectTagInfo(ObjectTag.contObjectTagKeyname).tagName(TEST_TAG_NAME + "-#2");
        objectTagInfoRepository.saveAndFlush(tagInfo2);

        restPortalContObjectMockMvc.perform(
            get("/api/object-tags/cont-objects/tag-info"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.arrayBeatifyResult(i)))
            .andExpect(jsonPath("$.[*].objectTagKeyname").value(hasItem(ObjectTag.contObjectTagKeyname)))
            .andExpect(jsonPath("$.[*].tagName").value(hasItem(TEST_TAG_NAME)))
            .andExpect(jsonPath("$.[*].tagColor").value(hasItem("GREEN")));

    }

    @Test
    @Transactional
    public void putObjectTagInfoList() throws Exception {

        ObjectTagInfo tagInfo = _createObjectTagInfo(ObjectTag.contObjectTagKeyname).tagName(TEST_TAG_NAME).tagColor("GREEN");
        List<ObjectTagInfoDTO> tagInfoDTOS = objectTagInfoMapper.toDto(Arrays.asList(tagInfo));

        restPortalContObjectMockMvc.perform(put("/api/object-tags/cont-objects/tag-info-list")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tagInfoDTOS)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(jsonPath("$.[*].objectTagKeyname").value(hasItem(ObjectTag.contObjectTagKeyname)))
            .andExpect(jsonPath("$.[*].tagName").value(hasItem(TEST_TAG_NAME)))
            .andExpect(jsonPath("$.[*].tagColor").value(hasItem("GREEN")));

    }

    @Test
    @Transactional
    public void putOneObjectTagInfo() throws Exception {

        ObjectTagInfo tagInfo = _createObjectTagInfo(ObjectTag.contObjectTagKeyname).tagName(TEST_TAG_NAME).tagColor("GREEN");
        ObjectTagInfoDTO tagInfoDTO = objectTagInfoMapper.toDto(tagInfo);

        restPortalContObjectMockMvc.perform(put("/api/object-tags/cont-objects/tag-info")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tagInfoDTO)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(jsonPath("$.objectTagKeyname").value(ObjectTag.contObjectTagKeyname))
            .andExpect(jsonPath("$.tagName").value(TEST_TAG_NAME))
            .andExpect(jsonPath("$.tagColor").value("GREEN"));

    }


    @Test
    @Transactional
    public void deleteContObjectsTagInfo() throws Exception {

        ObjectTagInfo tagInfo = _createObjectTagInfo(ObjectTag.contObjectTagKeyname).tagName(TEST_TAG_NAME).tagColor("GREEN");
        objectTagInfoRepository.saveAndFlush(tagInfo);

        restPortalContObjectMockMvc.perform(
            delete("/api/object-tags/cont-objects/tag-info/{tagName}", TEST_TAG_NAME))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());

    }



}
