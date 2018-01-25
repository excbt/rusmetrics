package ru.excbt.datafuse.nmk.service.handling;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;

import java.util.List;

@FunctionalInterface
public interface ConsumptionFunctionSupplier<T> {
    List<ConsumptionFunction<T>> supply(ContZPoint contZPoint);
}
