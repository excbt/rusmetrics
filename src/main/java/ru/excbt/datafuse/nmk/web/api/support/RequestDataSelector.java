package ru.excbt.datafuse.nmk.web.api.support;

import java.util.List;

import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;

public interface RequestDataSelector<T> {
	public List<T> selectData(Long contZPointId, TimeDetailKey timeDetail, LocalDatePeriod localDatePeriod);
}
