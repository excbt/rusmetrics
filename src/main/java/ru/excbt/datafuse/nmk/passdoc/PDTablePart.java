package ru.excbt.datafuse.nmk.passdoc;

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
public class PDTablePart {

    @Getter
    @Setter
    private PDPartType partType;

    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String caption;

    @Getter
    private final List<PDTableCell> elements = new ArrayList<>();

    public PDTableCell createElement() {
        PDTableCell result = new PDTableCell().tablePart(this);
        elements.add(result);
        return result;
    }

    public PDTableCell createElement(String caption) {
        PDTableCell result = new PDTableCell().tablePart(this);
        elements.add(result);
        return result.caption(caption);
    }

    public PDTableCell createValElement() {
        PDTableCell result = new PDTableCellValue().tablePart(this);
        elements.add(result);
        return result;
    }

    public void createValElements(int count) {
        for (int i = 0; i < count ; i++) {
            PDTableCell result = new PDTableCellValue().tablePart(this).keyValueIdx(i + 1); // keyValueIdx starts with 1
            elements.add(result);
        }
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    public int getTotalWidth() {
        int headerWidth = elements.size() == 0 ? 0 :
            elements.stream().map(i -> i.getTotalWidth()).filter(i -> i != null).mapToInt(Integer::intValue).sum();
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

}
