package ru.excbt.datafuse.nmk.data.service.energypassport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;
import ru.excbt.datafuse.nmk.passdoc.PDTable;
import ru.excbt.datafuse.nmk.passdoc.dto.PDTableValueCellsDTO;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by kovtonyk on 12.04.2017.
 */
public class EPSectionValueUtil {

    public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(EPSectionValueUtil.class);

    static {

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
        PDTableValueCellsDTO cellValuesDTO;
        try {
            cellValuesDTO = OBJECT_MAPPER.readValue(json, PDTableValueCellsDTO.class);
        } catch (IOException e) {
            return false;
        }
        return cellValuesDTO.checkComplexIdxs();
    }

}
