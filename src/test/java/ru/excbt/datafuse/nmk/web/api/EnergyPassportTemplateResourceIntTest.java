package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportTemplate;
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

    private EnergyPassportTemplate energyPassportTemplate;

    public static EnergyPassportTemplate createEnergyPassportTemplate() {
        EnergyPassportTemplate result = new EnergyPassportTemplate();
        result.setKeyname("TEST_" + System.currentTimeMillis());
        result.setDocumentDate(LocalDate.now());
        return result;
    }

    @Before
    public void setUp() throws Exception {
        energyPassportTemplate = createEnergyPassportTemplate();
    }

    @Test
    @Transactional
    public void testGetAll() throws Exception {
        EnergyPassportTemplate entity = createEnergyPassportTemplate();
        String checkDate = TestUtils.objectToJson(entity.getDocumentDate());

        energyPassportTemplateRepository.saveAndFlush(entity);

        ResultActions resultActions = _testGetJsonResultActions("/api/energy-passport-templates");
        resultActions.andExpect(jsonPath("$.[?(@.id==%d)].id", entity.getId()).exists());
        resultActions.andExpect(jsonPath("$.[?(@.id==%d)].documentDate",entity.getId()).value(TestUtils.removeQuotes(checkDate)));

    }

    @Test
    @Transactional
    public void testGetOne() throws Exception {
        EnergyPassportTemplate entity = createEnergyPassportTemplate();

        String json = TestUtils.objectToJson(entity);
        String checkDate = TestUtils.objectToJson(entity.getDocumentDate());

        energyPassportTemplateRepository.saveAndFlush(entity);
        ResultActions resultActions = _testGetJsonResultActions("/api/energy-passport-templates/" + energyPassportTemplate.getId());
        resultActions.andExpect(jsonPath("$.id").value(entity.getId()));
        resultActions.andExpect(jsonPath("$.documentDate").value(TestUtils.removeQuotes(checkDate)));
    }
}
