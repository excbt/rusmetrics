package ru.excbt.datafuse.nmk.passdoc;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 24.03.2017.
 */
public class PDTableCellValueInt extends PDTableCell<PDTableCellValueInt> {

    @Getter
    @Setter
    private Integer value;

    public PDTableCellValueInt(){
        super();
        setCellType(PDCellType.VALUE);
    }
}
