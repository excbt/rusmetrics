package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/**
 * Created by kovtonyk on 24.03.2017.
 */

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =  true)
@JsonPropertyOrder({ "key", "partType", "caption", "elements", "dynamic", "innerPdTable" })
public class PDTablePart implements PDReferable {


    @Getter
    @Setter
    @JsonIgnore
    private PDTable pdTable;

    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    private PDPartType partType;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String caption;

    @Getter
    @JsonInclude(value = Include.NON_EMPTY)
    private final List<PDTableCell> elements = new ArrayList<>();

    @Getter
    @Setter
    @JsonInclude(value = Include.NON_NULL)
    private PDTable innerPdTable;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private boolean dynamic;

    public PDTablePart(PDTable pdTable){
        this.pdTable = pdTable;
    }


    public PDTableCellValuePack createPackValueElement() {
        PDTableCellValuePack result = new PDTableCellValuePack().tablePart(this);
        elements.add(result);
        return result;
    }

    public PDTableCellStatic createStaticElement() {
        PDTableCellStatic result = new PDTableCellStatic().tablePart(this);
        elements.add(result);
        return result;
    }

    public PDTableCellStatic createStaticElement(String caption) {
        PDTableCellStatic result = new PDTableCellStatic().tablePart(this);
        elements.add(result);
        return result.caption(caption);
    }

//    public PDTableCell createValElement() {
//        PDTableCell result = new PDTableCellValueString().tablePart(this);
//        elements.add(result);
//        return result;
//    }

    public PDTableCellValueDoubleAggregation createIntValueAggrElement() {
        PDTableCellValueDoubleAggregation result = new PDTableCellValueDoubleAggregation().tablePart(this);
        elements.add(result);
        return result;
    }

    public void createValElements(int count) {
        for (int i = 0; i < count ; i++) {
            PDTableCell result = new PDTableCellValueString().tablePart(this).keyValueIdx(i + 1); // keyValueIdx starts with 1
            elements.add(result);
        }
    }

    public void createIntValueElements(int startWith, int count) {
        for (int i = startWith; i <= count ; i++) {
            PDTableCell result = new PDTableCellValueInteger().tablePart(this).keyValueIdx(i); // keyValueIdx starts with 1
            elements.add(result);
        }
    }

    public void createIntValueElements(int count) {
        createIntValueElements(1,count);
    }


    public PDTableCell<PDTableCellValueString> createStringValueElement() {
        return createValueElement(PDTableCellValueString.class);
    }

    public PDTableCell<PDTableCellValueDouble> createDoubleValueElement() {
        return createValueElement(PDTableCellValueDouble.class);
    }

    public PDTableCell<PDTableCellValueBoolean> createBooleanValueElement() {
        return createValueElement(PDTableCellValueBoolean.class);
    }


    public PDTableCell<?> createIntegerValueElement() {
        return createValueElement(PDTableCellValueInteger.class);
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    public Double get_totalWidth() {
        double headerWidth = elements.size() == 0 ? 0 :
            elements.stream().map(i -> i.get_totalWidth()).filter(i -> i != null).mapToDouble(Double::doubleValue).sum();
        return headerWidth;
    }

    public PDTablePart caption(String value){
        this.caption = value;
        return this;
    }

    public PDTablePart key(String value){
        this.key = value;
        return this;
    }

    public PDTablePart dynamic() {
        this.dynamic = true;
        return this;
    }

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public List<Double> get_columnWidths() {

        List<Double> headerWidths = new ArrayList<>();

        if (pdTable == null) {
            return headerWidths;
        }

        PDTablePart header = PDPartType.HEADER.equals(partType) ? this : pdTable.findHeader();

        if (header == null) {
            return headerWidths;
        }

        for (PDTableCell cell: header.elements) {
            headerWidths.addAll(cell.get_columnWidths());
        }

        if (PDPartType.HEADER.equals(partType)) {
            return headerWidths;
        }

        List<Double> result = new ArrayList<>();

        for (int i = 0; i < Math.min(elements.size(), headerWidths.size()) ; i++) {
            if (elements.get(i).getMergedCells() == 0) {
                Double v = headerWidths.get(i);
                if (v != null && v != 0) result.add(v);
            } else {
                Double mergedWidth = 0.0;
                for (int j = i; j < i + elements.get(i).getMergedCells(); j++) {
                    Double v = headerWidths.get(j);
                    if (v != null && v != 0) mergedWidth = mergedWidth + v;
                }
                if (mergedWidth != null && mergedWidth != 0) result.add(mergedWidth);
            }
        }


        return result;
    }


    public <T extends PDTableCell<T>> T createValueElement(final Class<T> valueType) {
        T result = null;
        if (PDTableCellValueInteger.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueInteger().tablePart(this);
        } else if (PDTableCellValueDouble.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueDouble().tablePart(this);
        } else if (PDTableCellValueString.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueString().tablePart(this);
        } else if (PDTableCellValueDoubleAggregation.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueDoubleAggregation().tablePart(this);
        } else if (PDTableCellValueBoolean.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueBoolean().tablePart(this);
        }

        if (result == null) {
            throw new UnsupportedOperationException();
        }

        elements.add(result);
        return result;
    }


    public <T extends PDTableCell<T>> List<T> createValueElements(int count, final Class<T> valueType) {
        List<T> result = new ArrayList<>();
        for (int i = 1; i <= count ; i++) {
            T element = createValueElement(valueType);
            element.keyValueIdx(i);
            result.add(element);
        }
        return result;
    };

    @Override
    public void linkInternalRefs() {
        for (PDTableCell<?> cell: elements) {
            cell.tablePart(this);
            cell.linkInternalRefs();
        }
    }


    public List<PDTableCell<?>> extractCellValues() {
        final List<PDTableCell<?>> result = new ArrayList<>();
        elements.forEach(i -> {
            if (i.getCellType() == PDCellType.VALUE) {
                result.add(i);
            }
        });
        return result;
    }

    public int maxRowSpan() {
        OptionalInt size = elements.stream().map(i -> i.childElementsLevel()).mapToInt(Integer::intValue).max();
        return size.isPresent() ? size.getAsInt() + 1 : 1;
    }

    public PDInnerTable createInnerTable() {
        if (this.partType != PDPartType.INNER_TABLE) {
            throw new IllegalStateException("Invalid part type for inner table");
        }
        PDInnerTable innerTable = new PDInnerTable(this);
        this.innerPdTable = innerTable;
        return innerTable;
    }


//    public static <T extends PDTableCell<T>> T createValueElement(final Class<T> valueType) {
//        T result = null;
//        if (PDTableCellValueInteger.class.isAssignableFrom(valueType)) {
//            result = (T) new PDTableCellValueInteger().tablePart(this);
//        } else if (PDTableCellValueDouble.class.isAssignableFrom(valueType)) {
//            result = (T) new PDTableCellValueDouble().tablePart(this);
//        } else if (PDTableCellValueString.class.isAssignableFrom(valueType)) {
//            result = (T) new PDTableCellValueString().tablePart(this);
//        } else if (PDTableCellValueDoubleAggregation.class.isAssignableFrom(valueType)) {
//            result = (T) new PDTableCellValueDoubleAggregation().tablePart(this);
//        } else if (PDTableCellValueBoolean.class.isAssignableFrom(valueType)) {
//            result = (T) new PDTableCellValueBoolean().tablePart(this);
//        }
//
//        if (result == null) {
//            throw new UnsupportedOperationException();
//        }
}
