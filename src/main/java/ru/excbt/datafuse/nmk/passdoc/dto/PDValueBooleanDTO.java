package ru.excbt.datafuse.nmk.passdoc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueBoolean;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueDoubleAggregation;

import javax.validation.constraints.DecimalMin;

/**
 * Created by kovtonyk on 28.03.2017.
 */
public class PDValueBooleanDTO extends PDValueDTO {

    public static String TYPE = "Boolean";

    @JsonInclude(value = Include.ALWAYS)
    @Getter
    @Setter
    private Boolean value;

    public static PDValueBooleanDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueBoolean.class, pdTableCell);
        PDTableCellValueBoolean srcValue = (PDTableCellValueBoolean) pdTableCell;
        PDValueBooleanDTO result = new PDValueBooleanDTO();
        result.setCommonProperties(srcValue);
        result.value = srcValue.getValue();
        return result;
    }

    @Override
    public String valueAsString() {
        return value != null ? value.toString() : null;
    }

    @Override
    public String dataType() {
        return TYPE;
    }
}
