package ru.excbt.datafuse.nmk.data.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.data.service.energypassport.EnergyPassport401_2014;
import ru.excbt.datafuse.nmk.web.api.EnergyPassportTemplateResourceIntTest;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
public class EnergyPassportServiceTest extends JpaSupportTest {

    @Autowired
    private EnergyPassportService energyPassportService;

    @Autowired
    private EnergyPassportTemplateRepository energyPassportTemplateRepository;

    @Test
    @Transactional
    public void testCreatePassportNew() throws Exception {
        EnergyPassportTemplate energyPassportTemplate = EnergyPassportTemplateResourceIntTest.createEnergyPassportTemplate();
        energyPassportTemplate.createSection((s) -> s.sectionKey("P_1.1").sectionOrder(1));
        energyPassportTemplateRepository.saveAndFlush(energyPassportTemplate);
        EnergyPassportDTO energyPassportDTO = energyPassportService.createPassport(energyPassportTemplate.getKeyname(), new Subscriber().id(getSubscriberId()));
        Assert.assertNotNull(energyPassportDTO);
        Assert.assertTrue(energyPassportDTO.getSections().size() == 1);
    }
}

