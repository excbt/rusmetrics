package ru.excbt.datafuse.nmk.service.handling;


import ru.excbt.datafuse.nmk.service.ConsumptionTask;

import java.util.List;

@FunctionalInterface
public interface ConsumptionTaskDataLoader<T> {
    List<T> load(ConsumptionTask task);
}
