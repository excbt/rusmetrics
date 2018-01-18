package ru.excbt.datafuse.nmk.service.handling;

import java.util.function.Function;

@FunctionalInterface
public interface ConsumptionValueMapper<T> extends Function<T, Double> {
    Double apply(T t);
}
