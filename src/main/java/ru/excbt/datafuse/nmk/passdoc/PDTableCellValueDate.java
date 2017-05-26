package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@JsonIgnoreProperties(ignoreUnknown =  true)
public class PDTableCellValueDate extends PDTableCell<PDTableCellValueDate> {

    @Getter
    @Setter
    private LocalDate value;

    public PDTableCellValueDate(){
        super();
        setCellType(PDCellType.VALUE);
    }
}
