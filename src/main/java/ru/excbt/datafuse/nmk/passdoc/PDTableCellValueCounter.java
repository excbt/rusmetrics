package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@JsonIgnoreProperties(ignoreUnknown =  true)
public class PDTableCellValueCounter extends PDTableCell<PDTableCellValueCounter> {

    public PDTableCellValueCounter(){
        super();
        setCellType(PDCellType.VALUE);
    }

    public Integer getValue() {
        return 1 + getRowIndex();
    }

}
