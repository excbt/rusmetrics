package ru.excbt.datafuse.nmk.service.handling;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConsumptionFunction<T> {

    public final static int DEFAULT_ROUND_SCALE = 7;

    private final String valueName;

    private final ConsumptionValueExtractor<T> valueExtractor;

    private final Predicate<T> dataFilter;

    private final String measureUnit;

    private final Predicate<T> notZeroFilter;

    public ConsumptionFunction(String valueName, Predicate<T> dataFilter, ConsumptionValueExtractor<T> valueExtractor, String measureUnit) {
        this.valueName = valueName;
        this.valueExtractor = valueExtractor;
        this.measureUnit = measureUnit;
        this.dataFilter = dataFilter;
        this.notZeroFilter = (i) -> valueExtractor.apply(i) != null && valueExtractor.apply(i) != 0;
    }

    public ConsumptionFunction(String valueName, ConsumptionValueExtractor<T> valueFunction, String measureUnit) {
        this(valueName,
            (i) -> Objects.nonNull(valueFunction.apply(i)),
            valueFunction,
            measureUnit);
    }


    public String getValueName() {
        return valueName;
    }

    public Function<T, Double> getValueExtractor() {
        return valueExtractor;
    }

    public Predicate<T> getDataFilter() {
        return dataFilter;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }


    public Double postProcessingRound(Double arg) {
        return roundScale(arg, roundScale(), BigDecimal.ROUND_CEILING);
    }


    public int roundScale() {
        return DEFAULT_ROUND_SCALE;
    }


    protected static Double roundScale(Double arg, int scale, int roundingMode) {
        if (arg == null) {
            return null;
        }
        BigDecimal bd = new BigDecimal(arg);
        bd = bd.setScale(scale, roundingMode);
        return bd.doubleValue();
    }

    public Predicate<T> getNotZeroFilter() {
        return notZeroFilter;
    }

    public static boolean valuesNotNull (Double ... values) {
        boolean result = true;
        for (int i = 0; i < values.length; i++) {
            result = result && Objects.nonNull(values[i]);
        }
        return result;
    }

    public static Double absNullSafe (Double arg1, Double arg2) {
        if (arg1 == null || arg2 == null) {
            return null;
        }
        return Math.abs(arg1 - arg2);
    }

}
