package ru.excbt.datafuse.nmk.passdoc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueDouble;

import javax.validation.constraints.DecimalMin;

/**
 * Created by kovtonyk on 28.03.2017.
 */
public class PDValueDoubleDTO extends PDValueDTO {

    @JsonInclude(value = Include.ALWAYS)
    @Getter
    @Setter
    @DecimalMin(value = "0")
    private Double value;

    public static PDValueDoubleDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueDouble.class, pdTableCell);
        PDTableCellValueDouble srcValue = (PDTableCellValueDouble) pdTableCell;
        PDValueDoubleDTO result = new PDValueDoubleDTO();
        result.setCommonProperties(srcValue);
        result.value = srcValue.getValue();
        return result;
    }
}
