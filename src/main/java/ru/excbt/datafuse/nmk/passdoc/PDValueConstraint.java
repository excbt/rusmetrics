package ru.excbt.datafuse.nmk.passdoc;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 03.05.2017.
 */
public class PDValueConstraint {

    public static final int MIN_YEAR = 2010;
    public static final int MAX_YEAR = 2100;
    public static final int MIN_PERCENT = 0;
    public static final int MAX_PERCENT = 100;


    @Getter
    @Setter
    private PDValueSubtype valueSubtype;

    public PDValueConstraint(PDValueSubtype valueSubtype) {
        this.valueSubtype = valueSubtype;
    }

    public PDValueConstraint() {
    }

//    @JsonIgnore
//    @Getter
//    private final Function<?, Boolean> check = (c) -> {
//
//      Preconditions.checkState(valueSubtype != null);
//
//      if (valueSubtype == PDValueSubtype.PERCENT) {
//          return checkPercent(c);
//      } else
//      if (valueSubtype == PDValueSubtype.YEAR) {
//          return checkYear(c);
//      } else
//          return true;
//
//    };

    public boolean check(Object cell) {
        Preconditions.checkState(valueSubtype != null);

        if (valueSubtype == PDValueSubtype.PERCENT) {
            return checkPercent(cell);
        } else
        if (valueSubtype == PDValueSubtype.YEAR) {
            return checkYear(cell);
        } else
            return true;
    }

    public static PDValueConstraint newPercentConstraint() {
        return new PDValueConstraint(PDValueSubtype.PERCENT);
    }

    public static PDValueConstraint newYearConstraint() {
        return new PDValueConstraint(PDValueSubtype.YEAR);
    }

    /**
     *
     * @param cell
     * @return
     */
    private static boolean checkPercent(Object cell) {
        return checkValueRange (cell, Double.valueOf(MIN_PERCENT) , Double.valueOf(MAX_PERCENT));
    }


    /**
     *
     * @param cell
     * @return
     */
    private static boolean checkYear(Object cell) {
        return checkValueRange (cell, Double.valueOf(MIN_YEAR) , Double.valueOf(MAX_YEAR));
    }


    /**
     *
     * @param cell
     * @param min
     * @param max
     * @return
     */
    private static boolean checkValueRange(Object cell, Double min, Double max) {
        if (!(cell instanceof PDValueObj)) {
            return false;
        }

        PDValueObj valueCell = (PDValueObj) cell;

        if (valueCell.getValue() == null) {
            return true;
        }
        if (valueCell.getValue() instanceof Integer) {
            return ((Integer) valueCell.getValue()) >= min && ((Integer) valueCell.getValue()) <= max;
        } else if (valueCell.getValue() instanceof Double) {
            return ((Double) valueCell.getValue()) >= min && ((Double) valueCell.getValue()) <= max;
        } else {
            throw new IllegalArgumentException("Unsupported cell value type. Supported cell values is Integer & Double");
        }
    }

}
