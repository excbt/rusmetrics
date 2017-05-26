package ru.excbt.datafuse.nmk.passdoc.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.ComplexIdx;
import ru.excbt.datafuse.nmk.passdoc.PDCellType;
import ru.excbt.datafuse.nmk.passdoc.PDConstants;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovtonyk on 28.03.2017.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY, property = "__type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PDValuePackDTO.class, name = "Pack"),
    @JsonSubTypes.Type(value = PDValueCounterDTO.class, name = "Counter"),
    @JsonSubTypes.Type(value = PDValueStringDTO.class, name = "String"),
    @JsonSubTypes.Type(value = PDValueIntegerDTO.class, name = "Integer"),
    @JsonSubTypes.Type(value = PDValueDoubleDTO.class, name = "Double"),
    @JsonSubTypes.Type(value = PDValueDoubleAggregationDTO.class, name = "DoubleAgg"),
    @JsonSubTypes.Type(value = PDValueBooleanDTO.class, name = "Boolean"),
    @JsonSubTypes.Type(value = PDValueDateDTO.class, name = "Date")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"__type", "cellType", "partKey", "keyValueIdx", "valuePackIdx"})
public abstract class PDValueDTO implements ComplexIdx{

    @Getter
    @Setter
    private PDCellType cellType;

    @Getter
    @Setter
    //@JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private int keyValueIdx;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @Getter
    @Setter
    private int valuePackIdx;

    @Getter
    @Setter
    private String partKey;

    @Getter
    @Setter
    private String columnKey;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @Getter
    @Setter
    private boolean _packed;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @Getter
    @Setter
    private boolean _dynamic;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @Getter
    @Setter
    private int _dynamicIdx;

    @Getter
    @Setter
    @JsonInclude(value = Include.NON_EMPTY)
    private List<PDValueConstraintDTO> constraints = new ArrayList<>();


//    @Getter
//    @Setter
//    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
//    private Integer _dynPartIdx;

    protected static <T extends PDTableCell> void checkValueTypeClass(Class<T> clazz, PDTableCell<?> tableCell) {
        if (!clazz.isAssignableFrom(tableCell.getClass())) {
            throw new IllegalArgumentException("tableCell is not of " + clazz.getSimpleName() + " class");
        }
    }

    protected void setCommonProperties(PDTableCell<?> tableCell) {
        this.cellType = tableCell.getCellType();
        this.keyValueIdx = tableCell.getKeyValueIdx();
        this.valuePackIdx = tableCell.getValuePackIdx();
        this.partKey = tableCell.getPartKey();
        this.columnKey = tableCell.getColumnKey();
        this._packed = tableCell.is_packed();
        this._dynamic = tableCell.is_dynamic();
        this._dynamicIdx = tableCell.get_dynamicIdx();
    }

    void cloneConstraints(PDTableCell<?> tableCell) {
        if (tableCell.getConstraints() != null) {
            tableCell.getConstraints().forEach((i) -> {
                PDValueConstraintDTO constraintDTO = new PDValueConstraintDTO();
                constraintDTO.setValueSubtype(i.getValueSubtype());
                this.constraints.add(constraintDTO);
            });
        }
    }

    @Override
    public String get_complexIdx() {
        StringBuilder sb = new StringBuilder();
        sb.append(partKey);
        if (_dynamic) {
            sb.append(PDConstants.COMPLEX_IDX_DYNAMIC_SUFFIX);
            sb.append(_dynamicIdx);
        }
        sb.append(PDConstants.COMPLEX_IDX_VALUE_IDX_SUFFIX + keyValueIdx);
        if (_packed) {
            sb.append(String.format(PDConstants.COMPLEX_IDX_VALUE_PACK_FORMAT, valuePackIdx));
//            sb.append("[");
//            sb.append(packValueIdx);
//            sb.append("]");
        }
        return sb.toString();
    }

    public abstract String valueAsString();

    public abstract String dataType();

    @Override
    public String toString() {
        return "PDValueDTO{" +
            "cellType=" + cellType +
            ", keyValueIdx=" + keyValueIdx +
            ", valuePackIdx=" + valuePackIdx +
            ", partKey='" + partKey + '\'' +
            ", columnKey='" + columnKey + '\'' +
            ", _packed=" + _packed +
            ", _dynamic=" + _dynamic +
            ", _dynamicIdx=" + _dynamicIdx +
            '}';
    }
}
