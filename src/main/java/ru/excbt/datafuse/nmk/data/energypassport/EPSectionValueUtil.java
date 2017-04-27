package ru.excbt.datafuse.nmk.data.energypassport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;
import ru.excbt.datafuse.nmk.passdoc.PDTable;
import ru.excbt.datafuse.nmk.passdoc.dto.PDTableValueCellsDTO;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Created by kovtonyk on 12.04.2017.
 */
public class EPSectionValueUtil {

    public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(EPSectionValueUtil.class);

    static {
        OBJECT_MAPPER.findAndRegisterModules();
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private EPSectionValueUtil() {
    }

    public static Optional<String> extractValues(String json) {
        return extractValues(json, false);
    }

    public static Optional<String> extractValues(String json, boolean pretty) {

        if (json == null) {
            return Optional.empty();
        }

        PDTableValueCellsDTO valueCellsDTO;

        PDTable pdTable1;
        try {
            pdTable1 = OBJECT_MAPPER.readValue(json, PDTable.class);
            pdTable1.linkInternalRefs();
        } catch (IOException e) {
            log.warn("Invalid input JSON. Can't parse it to PDTableValueCellsDTO");
            return Optional.empty();
        }

        valueCellsDTO = new PDTableValueCellsDTO();
        valueCellsDTO.addValueCells(pdTable1.extractCellValues());
        valueCellsDTO.sortElements();
        String result = JsonMapperUtils.objectToJson(valueCellsDTO, pretty);
        return Optional.of(result);

    }

    public static boolean validateJson(String json) {
        if (json == null) {
            return false;
        }
        Optional<PDTableValueCellsDTO> valueCellsDTO = jsonToValueCellsDTO(json);
        return valueCellsDTO.map((i) -> i.checkComplexIdxs()).orElse(false);
    }


    public static Optional<PDTableValueCellsDTO> jsonToValueCellsDTO (String json) {
        PDTableValueCellsDTO valuesCellsDTO;
        try {
            valuesCellsDTO = OBJECT_MAPPER.readValue(json, PDTableValueCellsDTO.class);
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(valuesCellsDTO);
    }


    public static String replaceJsonVars(String inJson, Properties vars) {
        String outJson = inJson;
        for (Map.Entry<Object, Object> entry : vars.entrySet())
        {
            //log.debug("found key: {}. Replace with: {}", entry.getKey(), entry.getValue());
            outJson = outJson.replaceAll((String)entry.getKey(),(String) entry.getValue());
        }
        return inJson.equals(outJson) ? inJson : outJson;
    }


}
