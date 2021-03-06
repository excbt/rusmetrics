package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.data.util.JsonMapperUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * Created by kovtonyk on 24.03.2017.
 */

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =  true)
@JsonPropertyOrder({ "key", "partType", "caption", "dynamic", "dynamicSuffix", "valueIdxSuffix", "elements", "innerPdTable" })
public class PDTablePart implements PDReferable {

    private static final Logger log = LoggerFactory.getLogger(PDTablePart.class);

    @JsonIgnore
    @Getter
    @Setter
    private PDTable pdTable;

    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    private PDPartType partType;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    private String caption;

    @JsonInclude(value = Include.NON_EMPTY)
    @Getter
    private final List<PDTableCell<?>> elements = new ArrayList<>();

    @JsonInclude(value = Include.NON_NULL)
    @Getter
    @Setter
    private PDInnerTable innerPdTable;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @Getter
    @Setter
    private boolean dynamic;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @Getter
    @Setter
    private String dynamicSuffix;

    @Getter
    private String valueIdxSuffix = PDConstants.COMPLEX_IDX_VALUE_IDX_SUFFIX;

    @Getter
    @Setter
    private boolean indentAfter = true;

    @Getter
    @Setter
    @JsonInclude(value = Include.NON_NULL)
    private PDAnchor anchor;

    private int keyValueIdxCounter = 0;

    public PDTablePart(PDTable pdTable){
        this.pdTable = pdTable;
    }


    public PDTableCellValuePack createValuePackElement() {
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

    public PDTableCell<PDTableCellValueDate> createDateValueElement() {
        return createValueElement(PDTableCellValueDate.class);
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

    public PDTablePart indentAfter(boolean value){
        this.indentAfter = value;
        return this;
    }

    public PDTablePart dynamic() {
        this.dynamic = true;
        this.dynamicSuffix = PDConstants.COMPLEX_IDX_DYNAMIC_SUFFIX;
        return this;
    }

    public PDTablePart anchor(PDAnchor anchor) {
        if (pdTable != null && !pdTable.anchorExists(anchor.getKey())) {
            this.anchor = anchor;
        }
        return this;
    }


    public PDTablePart and() {
        return this;
    }

    public PDTablePart applyCreator(Consumer<PDTablePart> creator) {
        if (creator != null) {
            creator.accept(this);
        }
        return this;
    }

    public PDTablePart widthsOfElements(double ... widths) {

        List<PDTableCell<?>> lowestElements = searchLowestElements();
        if (widths.length != lowestElements.size()) {
            log.error("widths.length ({}) != lowestElements.size() ({})", widths.length, lowestElements.size());
            lowestElements.forEach((i) -> {
                log.info("\n{}",JsonMapperUtils.objectToJson(i,true));
            });
        }
        Preconditions.checkState(widths.length == lowestElements.size());
        int idx = 0;
        for (PDTableCell<?> cell: lowestElements) {
            cell.width(widths[idx++]);
        }

        return this;
    }

    public PDTablePart widthsOfElements(int ... widths) {
        widthsOfElements(Arrays.stream(widths).asDoubleStream().toArray());
        return this;
    }

    private List<PDTableCell<?>> searchLowestElements() {
        List<PDTableCell<?>> result = new ArrayList<>();
        elements.forEach((e) -> result.addAll(e.searchLowestElements()));
        return result;
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
                    Double v = j < headerWidths.size() ? headerWidths.get(j) : null;
                    if (v != null && v != 0) mergedWidth = mergedWidth + v;
                }
                if (mergedWidth != null && mergedWidth != 0) result.add(mergedWidth);
            }
        }


        return result;
    }



    public boolean hasVerticalElements() {
        return elements.stream().filter(i -> i.isVertical()).findAny().isPresent();
    }


    public <T extends PDTableCell<T>> T createValueElement(final Class<T> valueType, boolean childElements) {
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
        } else if (PDTableCellValueCounter.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueCounter().tablePart(this);
        } else if (PDTableCellValueDate.class.isAssignableFrom(valueType)) {
            result = (T) new PDTableCellValueDate().tablePart(this);
        }

        if (result == null) {
            throw new UnsupportedOperationException();
        }
        if (!childElements) elements.add(result);
        return result;
    }

    public <T extends PDTableCell<T>> T createValueElement(final Class<T> valueType) {
        return createValueElement(valueType, false);
    }


    public <T extends PDTableCell<T>> List<T> createValueElements(int count, final Class<T> valueType) {
        List<T> result = new ArrayList<>();
        for (int i = 1; i <= count ; i++) {
            T element = createValueElement(valueType);
            element.keyValueIdx(i);
            result.add(element);
        }
        return result;
    }

    public <T extends PDTableCell<T>> List<T> createValueElements(int count, final Class<T> valueType, int keyValueStarts) {
        Preconditions.checkArgument(keyValueStarts >= 0);
        List<T> result = new ArrayList<>();
        for (int i = 1; i <= count ; i++) {
            T element = createValueElement(valueType);
            element.keyValueIdx(keyValueStarts + i - 1);
            result.add(element);
        }
        return result;
    }

    @Override
    public void linkInternalRefs() {
        if (innerPdTable != null) {
            innerPdTable.setParentTablePart(this);
            innerPdTable.linkInternalRefs();
        }
        for (PDTableCell<?> cell: elements) {
            cell.tablePart(this);
            cell.linkInternalRefs();
        }
    }


    private List<PDTableCell<?>> extractCellValues (List<PDTableCell<?>> elements) {
        final List<PDTableCell<?>> result = new ArrayList<>();
        for (PDTableCell<?> cell : elements) {
            if (cell.getCellType() == PDCellType.VALUE) {
                result.add(cell);
            }
            if (cell.getCellType() == PDCellType.VALUE_PACK) {
                result.addAll(extractCellValues(cell.getChildElements()));
            }
        }
        return result;
    }


    public List<PDTableCell<?>> extractCellValues() {
        final List<PDTableCell<?>> result = new ArrayList<>();

        result.addAll(extractCellValues(elements));

        if (innerPdTable != null) {
            result.addAll(innerPdTable.extractCellValues());
        }

        return result;
    }

    public int maxElementsLevel() {
        OptionalInt size = elements.stream().map(i -> i.childElementsLevel()).mapToInt(Integer::intValue).max();
        return size.isPresent() ? size.getAsInt() : 0;
    }

    public int maxRowSpan() {
        return maxElementsLevel() + 1;
    }

    public PDInnerTable createInnerTable() {
        if (this.partType != PDPartType.INNER_TABLE) {
            throw new IllegalStateException("Invalid part type for inner table");
        }
        PDInnerTable innerTable = new PDInnerTable(this);
        this.innerPdTable = innerTable;
        return innerTable;
    }


    int nextKeyValueIdx() {
        return ++keyValueIdxCounter;
    }

    public void resetKeyValueIdxCounter (){
        this.keyValueIdxCounter = 0;
    }
    public void initKeyValueIdxCounter(int value) {
       Preconditions.checkArgument(value >= 1);
        this.keyValueIdxCounter = value - 1;
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
