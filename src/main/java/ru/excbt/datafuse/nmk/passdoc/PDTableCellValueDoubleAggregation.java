package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 24.03.2017.
 */
@JsonIgnoreProperties(ignoreUnknown =  true)
public class PDTableCellValueDoubleAggregation extends PDTableCell<PDTableCellValueDoubleAggregation> {

    @Getter
    @Setter
    private Double value;

    @Getter
    @Setter
    private String function;

    @Getter
    @Setter
    private String group;

    public PDTableCellValueDoubleAggregation(){
        super();
        setCellType(PDCellType.VALUE);
    }
}
