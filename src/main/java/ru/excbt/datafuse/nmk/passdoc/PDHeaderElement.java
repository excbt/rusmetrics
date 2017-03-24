package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@NoArgsConstructor
public class PDHeaderElement {

    @Getter
    @Setter
    private String caption;
    private int width;

    @Getter
    @JsonProperty("elements")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private final List<PDHeaderElement> childElements = new ArrayList<>();

    private PDHeaderElement parent = null;

    public PDHeaderElement caption(String caption) {
        this.caption = caption;
        return this;
    }

    public PDHeaderElement width(int width) {
        this.width = width;
        return this;
    }

    public PDHeaderElement createChild() {
        PDHeaderElement child = new PDHeaderElement();
        childElements.add(child);
        child.parent = this;
        return child;
    }

    public PDHeaderElement createSubling() {
        checkState(parent != null);
        PDHeaderElement sibling = new PDHeaderElement();
        parent.childElements.add(sibling);
        sibling.parent = parent;
        return sibling;
    }

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public Integer getTotalWidth() {
        int childSum = childElements.size() == 0 ? 0 :
                        childElements.stream().map(i -> i.getTotalWidth()).filter(i -> i != null).mapToInt(Integer::intValue).sum();
        return this.width + childSum > 0 ? this.width + childSum : null;
    }

}

