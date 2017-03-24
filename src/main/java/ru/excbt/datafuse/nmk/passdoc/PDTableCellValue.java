package ru.excbt.datafuse.nmk.passdoc;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 24.03.2017.
 */
public class PDTableCellValue extends PDTableCell {
    @Getter
    @Setter
    private String value;

    public PDTableCellValue(){
        super();
        setCellType(PDCellType.VAL);
    }
}
