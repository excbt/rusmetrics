package ru.excbt.datafuse.nmk.passdoc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;
import ru.excbt.datafuse.nmk.passdoc.PDTableCellValueDoubleAggregation;

import javax.validation.constraints.DecimalMin;

/**
 * Created by kovtonyk on 28.03.2017.
 */
public class PDValueDoubleAggregationDTO extends PDValueDTO {

    public static final String TYPE = "DoubleAgg";

    @JsonInclude(value = Include.ALWAYS)
    @Getter
    @Setter
    @DecimalMin(value = "0")
    private Double value;

    @Getter
    @Setter
    private String valueFunction;

    @Getter
    @Setter
    private String valueGroup;

    public static PDValueDoubleAggregationDTO newInstance(PDTableCell<?> pdTableCell) {
        checkValueTypeClass(PDTableCellValueDoubleAggregation.class, pdTableCell);
        PDTableCellValueDoubleAggregation srcValue = (PDTableCellValueDoubleAggregation) pdTableCell;
        PDValueDoubleAggregationDTO result = new PDValueDoubleAggregationDTO();
        result.setCommonProperties(srcValue);
        result.value = srcValue.getValue();
        result.valueGroup = srcValue.getValueGroup();
        result.valueFunction = srcValue.getValueFunction();
        return result;
    }

    @Override
    public String valueAsString() {
        return value != null ? String.format("%f",value) : null;
    }

    @Override
    public String dataType() {
        return TYPE;
    }
}
