package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

/**
 * Created by kovtonyk on 03.04.2017.
 */
public class PDInnerTable extends PDTable {

    @Getter
    @JsonIgnore
    protected PDTablePart parentTablePart;

    public PDInnerTable(PDTablePart parentTablePart) {
        this.parentTablePart = parentTablePart;
    }

    public String getParentPartKey() {
        return parentTablePart != null ? parentTablePart.getKey() : null;
    }
}
