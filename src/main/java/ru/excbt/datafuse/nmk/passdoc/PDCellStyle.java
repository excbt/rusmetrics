package ru.excbt.datafuse.nmk.passdoc;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 25.04.2017.
 */
@Getter
@Setter
public class PDCellStyle {

    public enum HAlignment {LEFT, RIGHT, CENTER}

    private HAlignment hAlignment;

    public PDCellStyle hAlignment(HAlignment hAlignment) {
        this.hAlignment = hAlignment;
        return this;
    }
}
