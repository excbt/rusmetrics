package ru.excbt.datafuse.nmk.data.service.energypassport;

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
    public void test_Main() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014.createSection_Main();
        checkFactory(factory);
    }

    @Test
    public void test_Add_2_3() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.createSection_2_3();
        checkFactory(factory);
    }


    @Test
    public void test_Add_2_10() throws Exception {
        EnergyPassportSectionTemplateFactory factory = energyPassport401_2014_Add.createSection_2_10();

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
