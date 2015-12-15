package ru.excbt.datafuse.nmk.web.api.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;

public interface RequestDataSelectorPaged<T> {
	public Page<T> selectDataPaged(Long contZPointId, TimeDetailKey timeDetail, LocalDatePeriod localDatePeriod,
			PageRequest pageRequest);
}
