package ru.excbt.datafuse.nmk.data.service.energypassport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;
import ru.excbt.datafuse.nmk.passdoc.dto.PDTableValueCellsDTO;

import static org.junit.Assert.fail;

/**
 * Created by kovtonyk on 03.04.2017.
 */
public class EnergyPassportOrder401_2014Test {

    private static final Logger log = LoggerFactory.getLogger(EnergyPassportOrder401_2014Test.class);

    private EnergyPassportOrder401_2014 energyPassportOrder401_2014;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        energyPassportOrder401_2014 = new EnergyPassportOrder401_2014();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

    }


    @Test
    public void testS_M1() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_S_M1();
        checkFactory(factory);
//        EnergyPassportSectionTemplate template = factory.createSectionTemplate();
//        log.info("Json:\n{}", template.getSectionJson());

    }

    @Test
    public void testS_M2() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_S_M2();
        checkFactory(factory);
    }

    @Test
    public void testS_M3() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_S_M3();
        checkFactory(factory);
    }

    @Test
    public void testS_Main() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_Main();
        checkFactory(factory);
    }

    @Test
    public void test_S_2_10() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_2_10();

        checkFactory(factory);
        //EnergyPassportSectionTemplate template = factory.createSectionTemplate();

//        String tempalteJson = factory.createSectionTemplateJson(true);
//
//        log.info("Json:\n{}", tempalteJson);
//
//
//        String valuesJson = factory.createValuesJson(true);
//
//        log.info("ValuesJson:\n{}", valuesJson);
//
//
//        PDTableValueCellsDTO tableValueCellsDTO = mapper.readValue(valuesJson, PDTableValueCellsDTO.class);
    }

    @Test
    public void test_S_2_3() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassportOrder401_2014.createSection_2_3();
        //EnergyPassportSectionTemplate template = factory.createSectionTemplate();
        checkFactory(factory);
    }


    private void checkFactory(EnergyPassportSectionTemplateFactory factory) {
        String tempalteJson = factory.createSectionTemplateJson(true);
        log.info("Json:\n{}", tempalteJson);
        String valuesJson = factory.createValuesJson(true);
        log.info("ValuesJson:\n{}", valuesJson);
        try {
            PDTableValueCellsDTO tableValueCellsDTO = mapper.readValue(valuesJson, PDTableValueCellsDTO.class);
        } catch (Exception e) {
            fail();
        }

    }


}
