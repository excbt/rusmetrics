package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 25.04.2017.
 */
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class PDCellStyle {

    public enum HAlignment {LEFT, RIGHT, CENTER}

    private HAlignment hAlignment;

    private Integer rowSpan;

    private Boolean header1;

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

    public PDCellStyle header1() {
        this.header1 = true;
        return this;
    }


    public static PDCellStyle _makeHAligmentRight() {
        return new PDCellStyle().hAlignment(HAlignment.RIGHT);
    }

    public static PDCellStyle _makeHAligmentCenter() {
        return new PDCellStyle().hAlignment(HAlignment.CENTER);
    }

    public void copyStyle(PDCellStyle srcStyle) {
        Preconditions.checkNotNull(srcStyle);
        if (srcStyle.hAlignment != null) {
            this.hAlignment = srcStyle.hAlignment;
        }
        if (srcStyle.rowSpan != null) {
            this.rowSpan = srcStyle.rowSpan;
        }
        if (srcStyle.rowSpan != null) {
            this.header1 = srcStyle.header1;
        }
    }

}
