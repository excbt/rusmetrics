package ru.excbt.datafuse.nmk.data.energypassport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.passdoc.dto.PDTableValueCellsDTO;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by kovtonyk on 03.04.2017.
 */
public class EnergyPassport401_2014Test {

    private static final Logger log = LoggerFactory.getLogger(EnergyPassport401_2014Test.class);

    private EnergyPassport401_2014 energyPassport401_2014;
    private EnergyPassport401_2014_Add energyPassport401_2014_Add;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        energyPassport401_2014 = new EnergyPassport401_2014();
        energyPassport401_2014_Add = new EnergyPassport401_2014_Add();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

    }



    @Test
    public void test_S_1_2() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014.section_1_2();
        checkFactory(factory);
    }

    @Test
    public void test_S_1_3() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_1_3();
        checkFactory(factory);
    }

    @Test
    public void test_S_1_4() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_1_4();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_2() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_2();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_3() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_3();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_4() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_4();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_5() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_5();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_6() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_6();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_6b() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_6b();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_7() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_7();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_8() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_8();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_9() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_9();
        checkFactory(factory);
    }



    @Test
    public void test_S_2_10() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_10();

        checkFactory(factory);
        //EnergyPassportSectionTemplate template = factory.createSectionTemplate();

//        String tempalteJson = factory.createSectionTemplateJson(true);
//
//        log.info("Json:\n{}", tempalteJson);
//
//
//        String valuesJson = factory.createSectionValuesJson(true);
//
//        log.info("ValuesJson:\n{}", valuesJson);
//
//
//        PDTableValueCellsDTO tableValueCellsDTO = mapper.readValue(valuesJson, PDTableValueCellsDTO.class);
    }


    @Test
    public void test_S_2_11() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.section_2_11();
        checkFactory(factory);
    }


    private void checkFactory(EnergyPassportSectionTemplateFactory factory) {
        String templateJson = factory.createSectionTemplateJson(true);
        log.info("Json:\n{}", templateJson);
        String valuesJson = factory.createSectionValuesJson(true);
        log.info("ValuesJson:\n{}", valuesJson);
        try {
            PDTableValueCellsDTO tableValueCellsDTO = mapper.readValue(valuesJson, PDTableValueCellsDTO.class);
            assertTrue(tableValueCellsDTO.checkComplexIdxs());
            log.info("complexIdx:\n{}",tableValueCellsDTO.complexIdxs());
        } catch (Exception e) {
            fail();
        }

        Optional<String> extractedValues = EPSectionValueUtil.extractValues(templateJson,true);

        assertTrue(extractedValues.isPresent());
        assertEquals(valuesJson, extractedValues.get());

    }


}
