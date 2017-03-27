package ru.excbt.datafuse.nmk.passdoc;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 24.03.2017.
 */
public class PDTableCellStringValue extends PDTableCell<PDTableCellStringValue> {

    @Getter
    @Setter
    private String value;

    public PDTableCellStringValue(){
        super();
        setCellType(PDCellType.VAL);
    }
}
