package ru.excbt.datafuse.nmk.passdoc.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;

import java.time.LocalDate;

/**
 * Created by kovtonyk on 26.04.2017.
 */
public class PDValueDateDTOTest {

    private static final Logger log = LoggerFactory.getLogger(PDValueDateDTOTest.class);

    @Test
    public void testToJson() throws Exception {
        PDValueDateDTO dto = new PDValueDateDTO();
        dto.setValue(LocalDate.now());
        String json = JsonMapperUtils.objectToJson(dto, true);
        log.info("DTO:{}", json);
        PDValueDateDTO dto2 = JsonMapperUtils.jsonToObject(json, new TypeReference<PDValueDateDTO>() {
        });
        Assert.assertEquals(dto2.getValue(), dto.getValue());
    }
}
