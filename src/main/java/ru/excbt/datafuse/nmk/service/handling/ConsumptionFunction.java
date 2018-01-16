package ru.excbt.datafuse.nmk.service.handling;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConsumptionFunction<T> {

    public final static int DEFAULT_ROUND_SCALE = 7;

    private final String funcName;

    private final Function<T, Double> func;

    private final Predicate<T> filter;

    private final String measureUnit;

    public ConsumptionFunction(String funcName, Predicate<T> filter, Function<T, Double> func, String measureUnit) {
        this.funcName = funcName;
        this.func = func;
        this.measureUnit = measureUnit;
        this.filter = filter;
    }

    public String getFuncName() {
        return funcName;
    }

    public Function<T, Double> getFunc() {
        return func;
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
