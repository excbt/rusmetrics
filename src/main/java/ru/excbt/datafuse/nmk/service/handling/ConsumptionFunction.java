package ru.excbt.datafuse.nmk.service.handling;

import java.util.function.Function;

public class ConsumptionFunction<T> {

    private final String funcName;

    private final Function<T, Double> func;

    public ConsumptionFunction(String funcName, Function<T, Double> func) {
        this.funcName = funcName;
        this.func = func;
    }

    public String getFuncName() {
        return funcName;
    }

    public Function<T, Double> getFunc() {
        return func;
    }
}
