package ru.excbt.datafuse.nmk.data.model;


import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportTemplateDTO;
import ru.excbt.datafuse.nmk.utils.TestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by kovtonyk on 31.03.2017.
 */
public class EnergyPassportDTOTest {

    private final static ModelMapper modelMapper = new ModelMapper();

    private static final Logger log = LoggerFactory.getLogger(EnergyPassportDTOTest.class);


    public static EnergyPassportTemplate createEnergyPassportTemplate() {
        EnergyPassportTemplate result = new EnergyPassportTemplate();
        result.setKeyname("TEST_" + System.currentTimeMillis());
        result.setDocumentDate(LocalDate.now());
        return result;
    }

    @Test
    public void testSerialize() throws Exception {
        EnergyPassportTemplate entity = createEnergyPassportTemplate();
        entity.setId(100L);
        EnergyPassportTemplateDTO energyPassportTemplateDTO = modelMapper.map(entity,EnergyPassportTemplateDTO.class);
        assertEquals(entity.getId(), energyPassportTemplateDTO.getId());
        TestUtils.objectToJson(energyPassportTemplateDTO);
        TestUtils.objectToJson(LocalDateTime.now());
    }

}
