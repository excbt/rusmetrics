package ru.excbt.datafuse.nmk.web.api.support;

import org.springframework.data.domain.PageRequest;

import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;

/**
 * Интерфейс запроса данных по точке учета
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.12.2015
 * 
 * @param <T>
 */
public interface RequestAnyDataSelector<T> {

	public T selectData(Long contZPointId, TimeDetailKey timeDetail, LocalDatePeriod localDatePeriod,
			PageRequest pageRequest);

	public default T selectData(Long contZPointId, TimeDetailKey timeDetail, LocalDatePeriod localDatePeriod) {
		return selectData(contZPointId, timeDetail, localDatePeriod, null);
	}
}
