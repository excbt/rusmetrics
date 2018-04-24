package ru.excbt.datafuse.nmk.data.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrStPlanDTO;
import ru.excbt.datafuse.nmk.data.repository.SubscrStPlanRepository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.service.mapper.SubscrStPlanMapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class SubscrStPlanServiceTest extends PortalDataTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrStPlanServiceTest.class);

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
    }


    @Autowired
    private SubscrStPlanService service;

    @Autowired
    private SubscrStPlanMapper mapper;

    @Autowired
    private StPlanTemplateService stPlanTemplateService;

    @Autowired
    private SubscrStPlanRepository subscrStPlanRepository;

    @Test
    public void testFindStPlan() throws Exception {
        List<SubscrStPlanDTO> plansDTO = service.findStPlanDTOs(portalUserIdsService.getCurrentIds());
        assertNotNull(plansDTO);
    }

    @Test
    public void testMapper() throws Exception {
        SubscrStPlan plan = new SubscrStPlan();
        plan.setChartEnable(true);
        plan.setId(0L);
        plan.setSpName("MyName");
        plan.setSubscriberId(10L);
        plan.setRsoOrganization(new Organization().id(100L));
        plan.setLocalPlace(new LocalPlace().id(100L));
        SubscrStPlanDTO planDTO = mapper.toDto(plan);
        assertEquals(plan.getId(), planDTO.getId());
        assertEquals(plan.getSpName(), planDTO.getSpName());
        assertEquals(plan.getLocalPlace().getId(), planDTO.getLocalPlaceId());
        assertEquals(plan.getRsoOrganization().getId(), planDTO.getRsoOrganizationId());
        log.info("plabDTO: {}", planDTO.toString());
    }


    @Test
    @Ignore
    public void testCreateFromTemplate() throws Exception {
        SubscrStPlan stPlan = stPlanTemplateService.cloneFromTemplate("TEMP_CHART_001");
        SubscrStPlanDTO saved = service.saveStPlanDTO(mapper.toDto(stPlan), portalUserIdsService.getCurrentIds());
        assertNotNull(saved);

        SubscrStPlan checkSaved = subscrStPlanRepository.findOne(saved.getId());
        assertNotNull(checkSaved);
        assertNotNull(checkSaved.getPlanCharts());
//        assertTrue(checkSaved.getPlanCharts().size() > 0);

    }
}
