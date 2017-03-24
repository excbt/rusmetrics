package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@Data
public class PDTable {
    private String caption;


    private final List<PDTablePart> parts = new ArrayList<>();

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

    public PDTablePart createPart(PDPartType partType){
        PDTablePart newPart = new PDTablePart();
        newPart.setPartType(partType);
        parts.add(newPart);
        return newPart;
    }

}
