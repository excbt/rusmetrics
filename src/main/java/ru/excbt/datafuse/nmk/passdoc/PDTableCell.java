package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
    include=JsonTypeInfo.As.PROPERTY, property="@type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=PDTableCellValuePack.class, name="Pack"),
    @JsonSubTypes.Type(value=PDTableCellValueCounter.class, name="Counter"),
    @JsonSubTypes.Type(value=PDTableCellStatic.class, name="Static"),
    @JsonSubTypes.Type(value=PDTableCellValueString.class, name="String"),
    @JsonSubTypes.Type(value=PDTableCellValueInteger.class, name="Integer"),
    @JsonSubTypes.Type(value=PDTableCellValueDouble.class, name="Double"),
    @JsonSubTypes.Type(value=PDTableCellValueDoubleAggregation.class, name="DoubleAgg"),
    @JsonSubTypes.Type(value=PDTableCellValueBoolean.class, name="Boolean")
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
    //@JsonInclude(value = JsonInclude.Include.NON_NULL)
    private PDCellType cellType;

//    @Getter
//    @Setter
//    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
//    private boolean merged;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private int mergedCells;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private int keyValueIdx;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private int valuePackIdx;

    @Setter
    private String partKey;

    public T width(int value) {
        this.width = value;
        return (T) this;
    }

//    public T merged(Boolean value) {
//        this.merged = value;
//        return (T) this;
//    }
//
//    public T merged() {
//        this.merged = true;
//        return (T) this;
//    }

    public T mergedCells(int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        this.mergedCells = value;
        return (T) this;
    }

    public T keyValueIdx(int value) {
        this.keyValueIdx = value;
        return (T) this;
    }

    public T valuePackIdx(int value) {
        this.valuePackIdx = value;
        return (T) this;
    }

    public T tablePart(PDTablePart value) {
        this.tablePart = value;
        if (value != null) partKey = value.getKey();
        return (T) this;
    }

    public PDTablePart and() {
        return this.tablePart;
    }

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public Double get_totalWidth() {
        double childSum = childElements.size() == 0 ? 0 :
                        childElements.stream().map(i -> i.get_totalWidth()).filter(i -> i != null).mapToDouble(Double::doubleValue).sum();
        return this.width + childSum > 0 ? this.width + childSum : null;
    }


    protected List<Double> get_columnWidths() {
        List<Double> result = new ArrayList<>();
        if (childElements.size() == 0) {
            result.add(width);
        } else {
            for (PDTableCell cell: childElements) {
                result.addAll(cell.get_columnWidths());
            }
        }
        return result;
    }

    protected int topLevel() {
        int currentLevel = 0;
        PDTableCell<?> top = parent;
        while (top != null) {
            top = top.parent;
            currentLevel ++;
        }

        return currentLevel;

    }

    protected int childElementsLevel() {

        if (childElements.size() == 0) {
            return 0;
        }
        OptionalInt size = childElements.stream().map(i -> i.childElementsLevel()).mapToInt(Integer::intValue).max();
        return size.isPresent() ? size.getAsInt() + 1 : 0;
    }

    public int get_rowSpan() {
        int level = childElementsLevel();
        int maxSpan = tablePart != null ? tablePart.maxRowSpan() : 0;
        return  maxSpan - level - topLevel();
    }

    public int get_colSpan() {
        return mergedCells == 0 ? 1 : mergedCells;
    }

    @Override
    public void linkInternalRefs() {
        childElements.forEach(i -> {
            i.tablePart(this.tablePart);
            i.parent = this;
            i.linkInternalRefs();
        });
    }

    public String getPartKey() {
        return tablePart != null ? tablePart.getKey() : partKey;
    }

    public PDTableCellStatic createChild() {
        PDTableCellStatic child = new PDTableCellStatic().tablePart(this.tablePart);
        childElements.add(child);
        child.parent = this;
        return child;
    }

    public PDTableCellStatic createChild(String caption) {
        PDTableCellStatic child = new PDTableCellStatic().tablePart(this.tablePart);
        childElements.add(child);
        child.setCaption(caption);
        child.parent = this;
        return child;
    }

    public PDTableCellStatic createSibling() {
        checkState(parent != null);
        PDTableCellStatic sibling = new PDTableCellStatic().tablePart(this.tablePart);
        parent.childElements.add(sibling);
        sibling.parent = parent;
        return sibling;
    }

    public PDTableCellStatic createSibling(String caption) {
        checkState(parent != null);
        PDTableCellStatic sibling = new PDTableCellStatic().tablePart(this.tablePart);
        parent.childElements.add(sibling);
        sibling.setCaption(caption);
        sibling.parent = parent;
        return sibling;
    }

    public <T extends PDTableCell<T>> T createChildValue(final Class<T> valueType) {
        T child = this.getTablePart().createValueElement(valueType);
        childElements.add(child);
        child.parent = this;
        return child;
    }

    public <T extends PDTableCell<T>> T createSiblingValue(final Class<T> valueType) {
        checkState(parent != null);
        T sibling = this.getTablePart().createValueElement(valueType);
        parent.childElements.add(sibling);
        sibling.parent = parent;
        return sibling;
    }


}

