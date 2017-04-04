package ru.excbt.datafuse.nmk.passdoc.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueString;

/**
 * Created by kovtonyk on 28.03.2017.
 */
public class PDValuePackDTO extends PDValueDTO {

    public static PDValuePackDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueString.class, pdTableCell);
        PDTableCellValueString srcValue = (PDTableCellValueString) pdTableCell;
        PDValuePackDTO result = new PDValuePackDTO();
        result.setCommonProperties(srcValue);
        return result;
    }
}
