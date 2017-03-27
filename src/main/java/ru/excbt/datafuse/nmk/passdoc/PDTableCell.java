package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
    include=JsonTypeInfo.As.PROPERTY, property="@type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=PDTableCellStatic.class, name="Static"),
    @JsonSubTypes.Type(value=PDTableCellValueString.class, name="String"),
    @JsonSubTypes.Type(value=PDTableCellValueInt.class, name="Integer"),
    @JsonSubTypes.Type(value=PDTableCellValueDouble.class, name="Double"),
    @JsonSubTypes.Type(value=PDTableCellValueDoubleAggregation.class, name="DoubleAgg")
})
@NoArgsConstructor
public abstract class PDTableCell<T extends PDTableCell<T>> implements PDReferable {


    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @Getter
    private double width;

    @Getter
    @JsonProperty("elements")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    protected final List<PDTableCell> childElements = new ArrayList<>();

    protected PDTableCell parent;

    @Getter
    @JsonIgnore
    protected PDTablePart tablePart;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private PDCellType cellType;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private boolean merged;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private int keyValueIdx;

    public T width(int value) {
        this.width = value;
        return (T) this;
    }

    public T merged(Boolean value) {
        this.merged = value;
        return (T) this;
    }

    public T merged() {
        this.merged = true;
        return (T) this;
    }

    public T keyValueIdx(int value) {
        this.keyValueIdx = value;
        return (T) this;
    }

    public T tablePart(PDTablePart value) {
        this.tablePart = value;
        return (T) this;
    }

    public PDTablePart and() {
        return this.tablePart;
    }

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public Double getTotalWidth() {
        double childSum = childElements.size() == 0 ? 0 :
                        childElements.stream().map(i -> i.getTotalWidth()).filter(i -> i != null).mapToDouble(Double::doubleValue).sum();
        return this.width + childSum > 0 ? this.width + childSum : null;
    }


    protected List<Double> getColumnWidths() {
        List<Double> result = new ArrayList<>();
        if (childElements.size() == 0) {
            result.add(width);
        } else {
            for (PDTableCell cell: childElements) {
                result.addAll(cell.getColumnWidths());
            }
        }
        return result;
    }


    @Override
    public void linkInternalRefs() {
        childElements.forEach(i -> {
            i.tablePart(this.tablePart);
            i.parent = this;
            i.linkInternalRefs();
        });
    }

}

