package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovtonyk on 24.03.2017.
 */

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =  true)
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
    private final List<PDTableCell> elements = new ArrayList<>();


    public PDTablePart(PDTable pdTable){
        this.pdTable = pdTable;
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
            PDTableCell result = new PDTableCellValueInt().tablePart(this).keyValueIdx(i); // keyValueIdx starts with 1
            elements.add(result);
        }
    }

    public void createIntValueElements(int count) {
        createIntValueElements(1,count);
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    public Double getTotalWidth() {
        double headerWidth = elements.size() == 0 ? 0 :
            elements.stream().map(i -> i.getTotalWidth()).filter(i -> i != null).mapToDouble(Double::doubleValue).sum();
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

    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public List<Double> getColumnWidths() {
        List<Double> result = new ArrayList<>();
        List<Double> headerWidths = new ArrayList<>();

        if (pdTable == null) {
            return result;
        }

        PDTablePart header = PDPartType.HEADER.equals(partType) ? this : pdTable.findHeader();

        if (header == null) {
            return result;
        }

        for (PDTableCell cell: header.elements) {
            headerWidths.addAll(cell.getColumnWidths());
        }

        for (int i = 0; i < Math.min(elements.size(), headerWidths.size()) ; i++) {
            if (elements.get(i).isMerged() == false) {
                Double v = headerWidths.get(i);
                if (v != null && v != 0) result.add(v);
            } else {
                Double mergedWidth = 0.0;
                for (int j = i; j < headerWidths.size(); j++) {
                    Double v = headerWidths.get(j);
                    if (v != null && v != 0) mergedWidth = mergedWidth + v;
                }
                if (mergedWidth != null && mergedWidth != 0) result.add(mergedWidth);
            }
        }


        return result;
    }


    public <T extends PDTableCell> T createValueElement(final Class<T> valueType) {
        T result = null;
        if (PDTableCellValueInt.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueInt().tablePart(this);
        } else if (PDTableCellValueDouble.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueDouble().tablePart(this);
        } else if (PDTableCellValueString.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueString().tablePart(this);
        } else if (PDTableCellValueDoubleAggregation.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueDoubleAggregation().tablePart(this);
        }

        if (result == null) {
            throw new UnsupportedOperationException();
        }

        elements.add(result);
//
//        switch (valueType) {
//            case "valueInt" : result = (T) new PDTableCellValueInt().tablePart(this);
//                break;
//            case "valueString" : result = (T) new PDTableCellValueString().tablePart(this);
//                break;
//            default : result = null;
//        }
        return result;
    }


    public <T extends PDTableCell> List<T> createValueElements(int count, final Class<T> valueType) {
        List<T> result = new ArrayList<T>();
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


    public List<PDTableCell> extractCellValues() {
        final List<PDTableCell> result = new ArrayList<>();
        elements.forEach(i -> {
            if (i.getCellType() == PDCellType.VALUE) {
                result.add(i);
            }
        });
        return result;
    }

}
