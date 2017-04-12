package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 03.04.2017.
 */
public class PDInnerTable extends PDTable {

    @Getter
    @Setter
    @JsonIgnore
    protected PDTablePart parentTablePart;

    public PDInnerTable() {
    }

    public PDInnerTable(PDTablePart parentTablePart) {
        this.parentTablePart = parentTablePart;
    }

    public String getParentPartKey() {
        return parentTablePart != null ? parentTablePart.getKey() : null;
    }
}
