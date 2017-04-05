package ru.excbt.datafuse.nmk.passdoc.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueBoolean;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueCounter;

/**
 * Created by kovtonyk on 28.03.2017.
 */
public class PDValueCounterDTO extends PDValueDTO {

    @Getter
    @Setter
    private Integer value;

    public static PDValueCounterDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueCounter.class, pdTableCell);
        PDTableCellValueCounter srcValue = (PDTableCellValueCounter) pdTableCell;
        PDValueCounterDTO result = new PDValueCounterDTO();
        result.setCommonProperties(srcValue);
        result.value = srcValue.getValue();
        return result;
    }

}
