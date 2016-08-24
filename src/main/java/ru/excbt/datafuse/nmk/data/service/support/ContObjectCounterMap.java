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
public class ContObjectCounterMap {
	private final Map<Long, CounterInfo> notificationMap;

	public ContObjectCounterMap(List<CounterInfo> srcList) {
		checkNotNull(srcList);
		this.notificationMap = srcList.stream()
				.collect(Collectors.toMap(CounterInfo::getId, (info) -> info));
	}

	public long getCountValue(Long contObjectId) {
		CounterInfo info = notificationMap.get(contObjectId);
		return (info == null) || (info.getCount() == null) ? 0 : info.getCount().longValue();
	}
}