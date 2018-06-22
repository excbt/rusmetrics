package ru.excbt.datafuse.nmk.passdoc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueDate;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueInteger;

import javax.validation.constraints.DecimalMin;
import java.time.LocalDate;

/**
 * Created by kovtonyk on 25.04.2017.
 */
public class PDValueDateDTO extends PDValueDTO {

    public static final String TYPE = "Date";

    @JsonInclude(value = Include.ALWAYS)
    @Getter
    @Setter
    private LocalDate value;

    public static PDValueDateDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueDate.class, pdTableCell);
        PDTableCellValueDate srcValue = (PDTableCellValueDate) pdTableCell;
        PDValueDateDTO result = new PDValueDateDTO();
        result.setCommonProperties(srcValue);
        result.setValue(srcValue.getValue());
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
