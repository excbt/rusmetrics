package ru.excbt.datafuse.nmk.web.rest;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.service.mapper.OrganizationMapper;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class OrganizationResourceIntTest extends PortalApiTest {

    private static final Logger log = LoggerFactory.getLogger(OrganizationResourceIntTest.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationMapper organizationMapper;

    private OrganizationResource organizationResource;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        this.organizationResource = new OrganizationResource(portalUserIdsService, organizationService, organizationMapper);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(organizationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    @Test
    public void organizationsGet() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/organizations"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }

    @Test
    public void organizationsGetPage() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/organizations/page"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }

    @Test
    public void organizationGet() {
    }

    @Test
    @Transactional
    public void putOrganization() throws Exception {

        OrganizationDTO org = new OrganizationDTO();
        org.setOrganizationName("Новая организация");
        org.setOrganizationTypeId(1L);

        restPortalContObjectMockMvc.perform(
            put("/api/organizations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(org)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

    }


    @Test
    @Transactional
    public void organizationDelete() throws Exception {
        Organization org = new Organization();
        org.setOrganizationName("Test Organization");
        org.setSubscriber(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
        org = organizationRepository.save(org);

        Assertions.assertThat(org).isNotNull();
        Assertions.assertThat(org.getId()).isNotNull();

        restPortalContObjectMockMvc.perform(
            delete("/api/organizations/{organizationId}", org.getId()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());

    }
}
