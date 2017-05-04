package ru.excbt.datafuse.nmk.passdoc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

/**
 * Created by kovtonyk on 03.05.2017.
 */
public class PDValueConstraint {

    public static final int MIN_YEAR = 2010;
    public static final int MAX_YEAR = 2100;
    public static final int MIN_PERC = 0;
    public static final int MAX_PERC = 2100;


    @Getter
    @Setter
    private PDValueSubtype valueSubtype;

    public PDValueConstraint(PDValueSubtype valueSubtype) {
        this.valueSubtype = valueSubtype;
    }

    public PDValueConstraint() {
    }

    @JsonIgnore
    public final Predicate<PDTableCell<?>> check = (c) -> {

      Preconditions.checkState(valueSubtype != null);

      if (valueSubtype == PDValueSubtype.PERCENT) {
          return checkPercent(c);
      } else
      if (valueSubtype == PDValueSubtype.PERCENT) {
          return checkYear(c);
      } else
          return false;

    };

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
    private static boolean checkPercent(PDTableCell<?> cell) {
        return checkValueRange (cell, Double.valueOf(MIN_PERC) , Double.valueOf(MAX_PERC));
    }


    /**
     *
     * @param cell
     * @return
     */
    private static boolean checkYear(PDTableCell<?> cell) {
        return checkValueRange (cell, Double.valueOf(MIN_YEAR) , Double.valueOf(MAX_YEAR));
    }


    /**
     *
     * @param cell
     * @param min
     * @param max
     * @return
     */
    private static boolean checkValueRange (PDTableCell<?> cell, Double min, Double max) {
        if (cell instanceof PDValueObj) {
            PDValueObj valueCell = (PDValueObj) cell;
            if (valueCell.getValueObj() instanceof Integer) {
                return ((Integer) valueCell.getValueObj()) >= min && ((Integer) valueCell.getValueObj()) <= max;
            } else if (valueCell.getValueObj() instanceof Double) {
                return ((Double) valueCell.getValueObj()) >= min && ((Double) valueCell.getValueObj()) <= max;
            } else {
                throw new IllegalArgumentException("Unsupported cell value type. Supported cell values is Integer & Double");
            }
        } else
            return false;
    }

}
