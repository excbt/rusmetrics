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

    @Getter
    @Setter
    private String counterPrefix;

    public PDTableCellValueCounter(){
        super();
        setCellType(PDCellType.VALUE);
    }

    private int getCounterValue() {
//        List<PDTablePart> rows = this.tablePart.getPdTable().getParts()
//            .stream().filter(i -> PDPartType.ROW.equals(i.getPartType()) && i.isDynamic()).collect(Collectors.toList());
//        int idx = rows.indexOf(this.tablePart);
        return get_dynamicIdx();
    }

    public Integer getValue() {
        return getCounterValue();
    }

    public PDTableCellValueCounter counterPrefix(String value) {
        this.counterPrefix = value;
        return this;
    }

}
