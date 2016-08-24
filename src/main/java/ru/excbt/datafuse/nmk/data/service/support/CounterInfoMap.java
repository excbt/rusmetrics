package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author kovtonyk
 *
 */
public class CounterInfoMap {
	private final Map<Long, CounterInfo> notificationMap;

	public CounterInfoMap(List<CounterInfo> srcList) {
		checkNotNull(srcList);
		this.notificationMap = srcList.stream()
				.collect(Collectors.toMap(CounterInfo::getId, (info) -> info));
	}

	public long getCountValue(Long id) {
		CounterInfo info = notificationMap.get(id);
		return (info == null) || (info.getCount() == null) ? 0 : info.getCount().longValue();
	}
}