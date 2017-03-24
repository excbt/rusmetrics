package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class PDTableCell {

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String caption;

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    @Getter
    private int width;

    @Getter
    @JsonProperty("elements")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private final List<PDTableCell> childElements = new ArrayList<>();

    private PDTableCell parent = null;

    @Getter
    @JsonIgnore
    private PDTablePart tablePart = null;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private PDCellType cellType = PDCellType.STA;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private boolean merged;

    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private int keyValueIdx;

    public PDTableCell caption(String value) {
        this.caption = value;
        return this;
    }

    public PDTableCell width(int value) {
        this.width = value;
        return this;
    }

    public PDTableCell merged(Boolean value) {
        this.merged = value;
        return this;
    }

    public PDTableCell merged() {
        this.merged = true;
        return this;
    }

    public PDTableCell keyValueIdx(int value) {
        this.keyValueIdx = value;
        return this;
    }

    public PDTableCell tablePart(PDTablePart value) {
        this.tablePart = value;
        return this;
    }

    public PDTablePart and() {
        return this.tablePart;
    }

    public PDTableCell createChild() {
        PDTableCell child = new PDTableCell();
        childElements.add(child);
        child.parent = this;
        return child;
    }

    public PDTableCell createSubling() {
        checkState(parent != null);
        PDTableCell sibling = new PDTableCell();
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

