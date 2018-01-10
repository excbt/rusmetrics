package ru.excbt.datafuse.nmk.service.handling;

import java.util.function.Function;
import java.util.function.Predicate;

public class ConsumptionFunction<T> {

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

}
