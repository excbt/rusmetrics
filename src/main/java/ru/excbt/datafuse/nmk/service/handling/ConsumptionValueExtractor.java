package ru.excbt.datafuse.nmk.service.handling;

import java.util.function.Function;

public interface ConsumptionValueExtractor<T> extends Function<T, Double> {

}
