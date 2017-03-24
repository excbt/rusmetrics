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

    @JsonProperty("headers")
    private final List<PDHeaderElement> headerElements = new ArrayList<>();

    public PDHeaderElement createPDHeaderElement() {
        PDHeaderElement result = new PDHeaderElement();
        headerElements.add(result);
        return result;
    }

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public Integer getHeaderWidth() {
        int headerWidth = headerElements.size() == 0 ? 0 :
            headerElements.stream().map(i -> i.getTotalWidth()).filter(i -> i != null).mapToInt(Integer::intValue).sum();
        return headerWidth;
    }

}
