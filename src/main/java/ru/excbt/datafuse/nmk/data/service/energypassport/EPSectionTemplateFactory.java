package ru.excbt.datafuse.nmk.data.service.energypassport;

import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate;
import ru.excbt.datafuse.nmk.data.model.energypassport.EnergyPassportSectionTemplateFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;
import ru.excbt.datafuse.nmk.passdoc.PDTable;
import ru.excbt.datafuse.nmk.passdoc.dto.PDTableValueCellsDTO;

/**
 * Created by kovtonyk on 05.04.2017.
 */
public class EPSectionTemplateFactory implements EnergyPassportSectionTemplateFactory {

    private final PDTable pdTable;

    public EPSectionTemplateFactory(PDTable pdTable) {
        this.pdTable = pdTable;
    }

    @Override
    public String createSectionTemplateJson(Boolean pretty) {
        return JsonMapperUtils.objectToJson(pdTable, pretty);
    }

    @Override
    public String createValuesJson(boolean pretty) {
        PDTableValueCellsDTO valueCellsDTO = new PDTableValueCellsDTO();
        valueCellsDTO.addValueCells(pdTable.extractCellValues());
        valueCellsDTO.sortElements();
        return JsonMapperUtils.objectToJson(valueCellsDTO, pretty);

    }
}
