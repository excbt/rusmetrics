package ru.excbt.datafuse.nmk.service.handling;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConsumptionFunction<T> {

    public final static int DEFAULT_ROUND_SCALE = 7;

    private final String funcValueName;

    private final Function<T, Double> valueFunction;

    private final Predicate<T> filter;

    private final String measureUnit;

    public ConsumptionFunction(String funcValueName, Predicate<T> filter, Function<T, Double> valueFunction, String measureUnit) {
        this.funcValueName = funcValueName;
        this.valueFunction = valueFunction;
        this.measureUnit = measureUnit;
        this.filter = filter;
    }

    public String getValueName() {
        return funcValueName;
    }

    public Function<T, Double> getValueFunction() {
        return valueFunction;
    }

    public Predicate<T> getFilter() {
        return filter;
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

}
