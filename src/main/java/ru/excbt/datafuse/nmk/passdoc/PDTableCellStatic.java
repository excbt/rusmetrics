package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by kovtonyk on 27.03.2017.
 */
@JsonIgnoreProperties(ignoreUnknown =  true)
public class PDTableCellStatic extends PDTableCell<PDTableCellStatic> {


    @Getter
    @Setter
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String caption;


    public PDTableCellStatic() {
        super();
        setCellType(PDCellType.STATIC);
    }


    public PDTableCellStatic caption(String value) {
        this.caption = value;
        return this;
    }

}
