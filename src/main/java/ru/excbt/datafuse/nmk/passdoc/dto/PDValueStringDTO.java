package ru.excbt.datafuse.nmk.passdoc.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueString;

/**
 * Created by kovtonyk on 28.03.2017.
 */
public class PDValueStringDTO extends PDValueDTO {

    @Getter
    @Setter
    private String value;

    public static PDValueStringDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueString.class, pdTableCell);
        PDTableCellValueString srcValue = (PDTableCellValueString) pdTableCell;
        PDValueStringDTO result = new PDValueStringDTO();
        result.setCommonProperties(srcValue);
        result.setValue(srcValue.getValue());
        return result;
    }
}
