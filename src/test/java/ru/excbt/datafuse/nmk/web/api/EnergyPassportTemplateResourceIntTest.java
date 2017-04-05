package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportSectionTemplateRepository;
import ru.excbt.datafuse.nmk.data.repository.EnergyPassportTemplateRepository;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Created by kovtonyk on 30.03.2017.
 */
public class EnergyPassportTemplateResourceIntTest extends AnyControllerTest {

    private static final Logger log = LoggerFactory.getLogger(EnergyPassportTemplateResourceIntTest.class);

    @Autowired
    private EnergyPassportTemplateRepository energyPassportTemplateRepository;

    @Autowired
    private EnergyPassportSectionTemplateRepository energyPassportSectionTemplateRepository;

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

    @Before
    public void setUp() throws Exception {
//        energyPassportTemplate = createEnergyPassportTemplate();
    }

    @Test
    @Transactional
    public void testGetAll() throws Exception {
        EnergyPassportTemplate entity = createEnergyPassportTemplate();

        energyPassportTemplateRepository.saveAndFlush(entity);

        ResultActions resultActions = _testGetJsonResultActions("/api/energy-passport-templates");
        resultActions.andExpect(jsonPath("$.[?(@.id==%d)].id", entity.getId()).exists());
        resultActions.andExpect(jsonPath("$.[?(@.id==%d)].documentDate",entity.getId()).value(entity.getDocumentDate().toString()));

    }

    @Test
    @Transactional
    public void testGetOne() throws Exception {
        EnergyPassportTemplate entity = createEnergyPassportTemplate();

        EnergyPassportSectionTemplate s1 = entity.createSection((s) -> s.sectionKey("P_1.1").sectionOrder(1));
        String json = TestUtils.objectToJson(entity);

        energyPassportTemplateRepository.saveAndFlush(entity);
        ResultActions resultActions = _testGetJsonResultActions("/api/energy-passport-templates/" + entity.getId());
        resultActions.andExpect(jsonPath("$.id").value(entity.getId()));
        resultActions.andExpect(jsonPath("$.documentDate").value(entity.getDocumentDate().toString()));
    }

    @Test
    @Transactional
    public void testGetNew() throws Exception {
        ResultActions resultActions = _testGetJsonResultActions("/api/energy-passport-templates/new");
        resultActions.andExpect(jsonPath("$.documentDate").value(LocalDate.of(2014,6,30).toString()));
    }
}
