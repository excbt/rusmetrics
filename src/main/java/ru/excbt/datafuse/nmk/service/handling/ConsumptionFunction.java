package ru.excbt.datafuse.nmk.service.handling;

import java.util.function.Function;

public class ConsumptionFunction<T> {

    private final String funcName;

    private final Function<T, Double> func;

    private final String measureUnit;

    public ConsumptionFunction(String funcName, Function<T, Double> func, String measureUnit) {
        this.funcName = funcName;
        this.func = func;
        this.measureUnit = measureUnit;
    }

    public String getFuncName() {
        return funcName;
    }

    public Function<T, Double> getFunc() {
        return func;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }
}
