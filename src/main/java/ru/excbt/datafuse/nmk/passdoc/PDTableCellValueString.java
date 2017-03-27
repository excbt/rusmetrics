package ru.excbt.datafuse.nmk.passdoc;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 24.03.2017.
 */
public class PDTableCellValueString extends PDTableCell<PDTableCellValueString> {

    @Getter
    @Setter
    private String value;

    public PDTableCellValueString(){
        super();
        setCellType(PDCellType.VALUE);
    }
}
