package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =  true)
@JsonPropertyOrder({ "caption", "shortCaption", "viewType", "sectionKey", "parts"})
public class PDTable implements PDReferable {

    private static final Logger log = LoggerFactory.getLogger(PDTable.class);

    @Getter
    @Setter
    private String caption;

    @Getter
    @Setter
    private String shortCaption;

    @Getter
    @Setter
    private String sectionHeader;

    @Getter
    @Setter
    private String sectionNr;

    @Getter
    private final List<PDTablePart> parts = new ArrayList<>();

    @Getter
    @Setter
    private PDViewType viewType;

    @Getter
    @Setter
    private String sectionKey;


    public PDTable caption(String value) {
        this.caption = value;
        return this;
    }

    public PDTable shortCaption(String value) {
        this.shortCaption = value;
        return this;
    }

    public PDTable viewType(PDViewType value) {
        this.viewType = value;
        return this;
    }

    public PDTable sectionKey(String value) {
        this.sectionKey = value;
        return this;
    }

    public PDTable sectionHeader(String value) {
        this.sectionHeader = value;
        return this;
    }

    public PDTable sectionNr(String value) {
        this.sectionNr = value;
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
        for (PDTablePart part : parts) {
            List<PDTableCell<?>> partValues = part.extractCellValues();
            if (!partValues.isEmpty()) {
                result.addAll(partValues);
            }
        }
        log.info("Size of values: {}", result.size());
        return result;
    }

}
