package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@JsonIgnoreProperties(ignoreUnknown =  true)
public class PDTableCellValueString extends PDTableCell<PDTableCellValueString> {

    @Getter
    @Setter
    private String value;

    public PDTableCellValueString(){
        super();
        setCellType(PDCellType.VALUE);
    }
}
