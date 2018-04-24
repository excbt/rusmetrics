package ru.excbt.datafuse.nmk.data.service;

import org.junit.Assert;
import org.junit.Before;
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
import ru.excbt.datafuse.nmk.data.energypassport.EnergyPassport401_2014_Add;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportTemplateDTO;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.api.EnergyPassportResourceIntTest;
import ru.excbt.datafuse.nmk.web.api.EnergyPassportTemplateResourceIntTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@RunWith(SpringRunner.class)
public class EnergyPassportServiceTest extends PortalDataTest {

    private static final Logger log = LoggerFactory.getLogger(EnergyPassportServiceTest.class);


    @Mock
    private PortalUserIdsService portalUserIdsService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
    }


    @Autowired
    private EnergyPassportService energyPassportService;

    @Autowired
    private EnergyPassportTemplateRepository energyPassportTemplateRepository;

    @Autowired
    private EnergyPassportTemplateService energyPassportTemplateService;

    @Test
    public void testCreatePassportNew() throws Exception {
        EnergyPassportTemplate energyPassportTemplate = EnergyPassportTemplateResourceIntTest.createEnergyPassportTemplate();
        energyPassportTemplate.createSection((s) -> s.sectionKey("P_1.1").sectionOrder(1).sectionJson(EnergyPassportResourceIntTest.DUMMY_JSON));
        energyPassportTemplateRepository.saveAndFlush(energyPassportTemplate);
        EnergyPassportDTO energyPassportDTO = energyPassportService.createPassport(energyPassportTemplate.getKeyname(),
            new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
        Assert.assertNotNull(energyPassportDTO);
        Assert.assertTrue(energyPassportDTO.getSections().size() == 1);
    }

    @Test
    public void testUpdatingPassportFromTemplate() throws Exception {
        EnergyPassportTemplateDTO energyPassportTemplateDTO = energyPassportTemplateService.createNewDTO_401();
        energyPassportTemplateDTO.setKeyname(EnergyPassport401_2014_Add.ENERGY_DECLARATION_1);
        EnergyPassportTemplateDTO resultPassportTemplateDTO = energyPassportTemplateService.saveEnergyPassportTemplate(energyPassportTemplateDTO);
        log.info("PassportTemplateId = {}", resultPassportTemplateDTO.getId());
        energyPassportService.updateExistingEnergyPassportsFromTemplate(resultPassportTemplateDTO.getId());
    }


}

