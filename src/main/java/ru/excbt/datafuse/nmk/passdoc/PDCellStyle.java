package ru.excbt.datafuse.nmk.passdoc;

import com.google.common.base.Preconditions;
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

    private Integer rowSpan;

    public PDCellStyle hAlignment(HAlignment hAlignment) {
        this.hAlignment = hAlignment;
        return this;
    }

    public PDCellStyle rowSpan(Integer rowSpan) {
        Preconditions.checkNotNull(rowSpan);
        Preconditions.checkArgument(rowSpan > 0);
        this.rowSpan = rowSpan;
        return this;
    }
}
