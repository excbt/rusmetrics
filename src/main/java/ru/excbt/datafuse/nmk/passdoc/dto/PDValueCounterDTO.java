package ru.excbt.datafuse.nmk.passdoc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueBoolean;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueCounter;

/**
 * Created by kovtonyk on 28.03.2017.
 */
public class PDValueCounterDTO extends PDValueDTO {

    public static final String TYPE = "Counter";

    @JsonInclude(value = Include.ALWAYS)
    @Getter
    @Setter
    private Integer value;

    @Getter
    @Setter
    private String counterPrefix;

    public static PDValueCounterDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueCounter.class, pdTableCell);
        PDTableCellValueCounter srcValue = (PDTableCellValueCounter) pdTableCell;
        PDValueCounterDTO result = new PDValueCounterDTO();
        result.setCommonProperties(srcValue);
        result.value = srcValue.getValue();
        result.counterPrefix = srcValue.getCounterPrefix();
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
