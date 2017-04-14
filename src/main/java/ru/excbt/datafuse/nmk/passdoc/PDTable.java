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

    public static final String PREFIX = "P_";

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

    public PDTablePart createPartInnerTable(){
        PDTablePart newPart = new PDTablePart(this);
        newPart.setPartType(PDPartType.INNER_TABLE);
        parts.add(newPart);
        return newPart;
    }

    public PDTablePart findHeader() {
        if (parts.isEmpty()) {
            return null;
        }
        Optional<PDTablePart> result = parts.stream().filter(i -> PDPartType.HEADER.equals(i.getPartType())).findFirst();
        // if we cannot find header, then return first part
        return result.isPresent() ? result.get() : parts.get(0);
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
        return result;
    }


    public PDTablePart createPartLine(String nr) {
        String key = PREFIX + (nr.charAt(nr.length() - 1) == '.' ? nr.substring(0, nr.length() - 1) : nr);

        return createPart(PDPartType.SIMPLE_LINE).key(key).createStaticElement(nr).and();
    }

}
