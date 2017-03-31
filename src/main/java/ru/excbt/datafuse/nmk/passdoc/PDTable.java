package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =  true)
public class PDTable implements PDReferable {
    private String caption;

    @Getter
    private final List<PDTablePart> parts = new ArrayList<>();

    @Getter
    @Setter
    private PDViewType viewType;

    @Getter
    @Setter
    private String sectionKey;

//    @JsonProperty("headers")
//    private final List<PDTableCell> headerElements = new ArrayList<>();

//    public PDTableCell createPDHeaderElement() {
//        PDTableCell result = new PDTableCell();
//        headerElements.add(result);
//        return result;
//    }
//
//    @JsonInclude(value = JsonInclude.Include.NON_NULL)
//    public Integer getHeaderWidth() {
//        int headerWidth = headerElements.size() == 0 ? 0 :
//            headerElements.stream().map(i -> i.getTotalWidth()).filter(i -> i != null).mapToInt(Integer::intValue).sum();
//        return headerWidth;
//    }

    public PDTable viewType(PDViewType value) {
        this.viewType = value;
        return this;
    }

    public PDTable sectionKey(String value) {
        this.sectionKey = value;
        return this;
    }

    public PDTablePart createPart(PDPartType partType){
        PDTablePart newPart = new PDTablePart(this);
        newPart.setPartType(partType);
        parts.add(newPart);
        return newPart;
    }

    public PDTablePart findHeader() {
        Optional<PDTablePart> result = parts.stream().filter(i -> PDPartType.HEADER.equals(i.getPartType())).findFirst();
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public void linkInternalRefs() {
        for (PDTablePart part: parts) {
            part.setPdTable(this);
            part.linkInternalRefs();
        }
    }

    public List<PDTableCell<?>> extractCellValues() {
        List<PDTableCell<?>> result = new ArrayList<>();
        parts.forEach(i -> {
            List<PDTableCell<?>> partValues = i.extractCellValues();
            if (!partValues.isEmpty()) {
                result.addAll(partValues);
            }
        });
        return result;
    }

}
