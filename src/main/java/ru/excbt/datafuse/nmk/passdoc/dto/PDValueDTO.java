package ru.excbt.datafuse.nmk.passdoc.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.passdoc.PDCellType;
import ru.excbt.datafuse.nmk.passdoc.PDTableCell;

/**
 * Created by kovtonyk on 28.03.2017.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
    include=JsonTypeInfo.As.PROPERTY, property="__type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=PDValuePackDTO.class, name="Pack"),
    @JsonSubTypes.Type(value=PDValueCounterDTO.class, name="Counter"),
    @JsonSubTypes.Type(value=PDValueStringDTO.class, name="String"),
    @JsonSubTypes.Type(value=PDValueIntegerDTO.class, name="Integer"),
    @JsonSubTypes.Type(value=PDValueDoubleDTO.class, name="Double"),
    @JsonSubTypes.Type(value=PDValueDoubleAggregationDTO.class, name="DoubleAgg"),
    @JsonSubTypes.Type(value=PDValueBooleanDTO.class, name="Boolean")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"__type", "cellType", "keyValueIdx", "packValueIdx", "partKey"})
public abstract class PDValueDTO {

    @Getter
    private final PDCellType cellType = PDCellType.VALUE;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private int keyValueIdx;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private int packValueIdx;

    @Getter
    @Setter
    private String partKey;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private boolean _packed;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private boolean _dynamic;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private int _dynamicIdx;


    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private Integer dynPartIdx;

    protected void setCommonProperties(PDTableCell<?> tableCell) {
        this.keyValueIdx = tableCell.getKeyValueIdx();
        this.packValueIdx = tableCell.getPackValueIdx();
        this.partKey = tableCell.getPartKey();
        this._packed = tableCell.is_packed();
        this._dynamic = tableCell.is_dynamic();
        this._dynamicIdx = tableCell.get_dynamicIdx();
    }

    protected static <T extends PDTableCell> void checkValueTypeClass (Class<T> clazz, PDTableCell<?> tableCell) {
        if (!clazz.isAssignableFrom(tableCell.getClass())) {
            throw new IllegalArgumentException("tableCell is not of " + clazz.getSimpleName() + " class");
        }
    }

}
