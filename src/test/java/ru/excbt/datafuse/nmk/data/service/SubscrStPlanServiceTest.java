package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrStPlanDTO;
import ru.excbt.datafuse.nmk.service.mapper.SubscrStPlanMapper;

import java.util.List;

import static org.junit.Assert.*;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class SubscrStPlanServiceTest extends JpaSupportTest {

    private static final Logger log = LoggerFactory.getLogger(SubscrStPlanServiceTest.class);

    @Autowired
    private SubscrStPlanService service;

    @Autowired
    private SubscrStPlanMapper mapper;

    @Autowired
    private StPlanTemplateService stPlanTemplateService;

    @Test
    public void testFindStPlan() throws Exception {
        List<SubscrStPlanDTO> plansDTO = service.findStPlanDTOs(getSubscriberParam());
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
    public void testCreateFromTemplate() throws Exception {
        SubscrStPlan stPlan = stPlanTemplateService.cloneFromTemplate("TEMP_CHART_001");
        SubscrStPlan saved = service.save(stPlan, getSubscriberParam());
        assertNotNull(saved);
    }
}
