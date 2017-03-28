package ru.excbt.datafuse.nmk.passdoc.dto;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueInteger;

import javax.validation.constraints.DecimalMin;

/**
 * Created by kovtonyk on 28.03.2017.
 */
public class PDValueIntegerDTO extends PDValueDTO {

    @Getter
    @Setter
    @DecimalMin(value = "0")
    private Integer value;

    public static PDValueIntegerDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueInteger.class, pdTableCell);
        PDTableCellValueInteger srcValue = (PDTableCellValueInteger) pdTableCell;
        PDValueIntegerDTO result = new PDValueIntegerDTO();
        result.setCommonProperties(srcValue);
        return result;
    }

}
