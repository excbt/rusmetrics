package ru.excbt.datafuse.nmk.passdoc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueInteger;

import javax.validation.constraints.DecimalMin;

/**
 * Created by kovtonyk on 28.03.2017.
 */
public class PDValueIntegerDTO extends PDValueDTO {

    @JsonInclude(value = Include.ALWAYS)
    @DecimalMin(value = "0")
    @Getter
    @Setter
    private Integer value;

    public static PDValueIntegerDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueInteger.class, pdTableCell);
        PDTableCellValueInteger srcValue = (PDTableCellValueInteger) pdTableCell;
        PDValueIntegerDTO result = new PDValueIntegerDTO();
        result.setCommonProperties(srcValue);
        return result;
    }

}
