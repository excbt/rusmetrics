package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@JsonIgnoreProperties(ignoreUnknown =  true)
public class PDTableCellValueInteger extends PDTableCell<PDTableCellValueInteger> implements PDValueObj {

    @Getter
    @Setter
    private Integer value;

    public PDTableCellValueInteger(){
        super();
        setCellType(PDCellType.VALUE);
    }

    @Override
    public Object getValueObj() {
        return value;
    }
}
