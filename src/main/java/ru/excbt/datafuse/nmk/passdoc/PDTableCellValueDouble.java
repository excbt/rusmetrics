package ru.excbt.datafuse.nmk.passdoc;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 24.03.2017.
 */
public class PDTableCellValueDouble extends PDTableCell<PDTableCellValueDouble> {

    @Getter
    @Setter
    private Double value;

    public PDTableCellValueDouble(){
        super();
        setCellType(PDCellType.VALUE);
    }
}
