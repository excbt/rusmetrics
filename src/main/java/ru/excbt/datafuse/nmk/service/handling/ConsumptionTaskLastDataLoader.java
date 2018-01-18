package ru.excbt.datafuse.nmk.service.handling;

import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTask;

import java.util.List;

@FunctionalInterface
public interface ConsumptionTaskLastDataLoader<T> {
    List<T> load(ConsumptionTask task, List<Long> ids);
}
