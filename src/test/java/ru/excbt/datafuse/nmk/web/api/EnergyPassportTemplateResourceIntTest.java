package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportSectionTemplateRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.EnergyPassportTemplateService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.EnergyPassportTemplateResource;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by kovtonyk on 30.03.2017.
 */
@RunWith(SpringRunner.class)
public class EnergyPassportTemplateResourceIntTest extends PortalApiTest {

    private static final Logger log = LoggerFactory.getLogger(EnergyPassportTemplateResourceIntTest.class);

    @Autowired
    private EnergyPassportTemplateRepository energyPassportTemplateRepository;

    @Autowired
    private EnergyPassportSectionTemplateRepository energyPassportSectionTemplateRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private EnergyPassportTemplateResource energyPassportTemplateResource;
    @Mock
    private PortalUserIdsService portalUserIdsService;

    private MockMvcRestWrapper mockMvcRestWrapper;
    @Autowired
    private EnergyPassportTemplateService energyPassportTemplateService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        energyPassportTemplateResource = new EnergyPassportTemplateResource(energyPassportTemplateService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(energyPassportTemplateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
    }


//    private EnergyPassportTemplate energyPassportTemplate;

    public static EnergyPassportTemplate createEnergyPassportTemplate() {
        EnergyPassportTemplate result = new EnergyPassportTemplate();
        result.setKeyname("TEST_" + System.currentTimeMillis());
        result.setDocumentDate(LocalDate.now());
        return result;
    }

    public static EnergyPassportSectionTemplate createEnergyPassportSectionTemplate() {
        EnergyPassportSectionTemplate result = new EnergyPassportSectionTemplate();
        result.setSectionKey("P_1.1");
        result.setSectionOrder(1);
        return result;
    }

    @Test
    @Transactional
    public void testGetAll() throws Exception {
        EnergyPassportTemplate entity = createEnergyPassportTemplate();

        energyPassportTemplateRepository.saveAndFlush(entity);
        mockMvcRestWrapper.restRequest("/api/energy-passport-templates")
            .testGetAndReturn()
            .andExpect(jsonPath("$.[?(@.id==%d)].id", entity.getId()).exists());

    }

    @Test
    @Transactional
    public void testGetOne() throws Exception {
        EnergyPassportTemplate entity = createEnergyPassportTemplate();

        EnergyPassportSectionTemplate s1 = entity.createSection((s) -> s.sectionKey("P_1.1").sectionJson(EnergyPassportResourceIntTest.DUMMY_JSON).sectionOrder(1));
        String json = TestUtils.objectToJson(entity);

        energyPassportTemplateRepository.saveAndFlush(entity);
        mockMvcRestWrapper.restRequest("/api/energy-passport-templates/{id}", entity.getId())
            .testGetAndReturn()
            .andExpect(jsonPath("$.id").value(entity.getId()));
    }

    @Test
    @Transactional
    public void testGetNew() throws Exception {
        mockMvcRestWrapper.restRequest("/api/energy-passport-templates/new").testGet();
    }

    @Test
    @Transactional
    public void testGetNewData() throws Exception {
        mockMvcRestWrapper.restRequest("/api/energy-passport-templates/newData").testGet();
    }
}
