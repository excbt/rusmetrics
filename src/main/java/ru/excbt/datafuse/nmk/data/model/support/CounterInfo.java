package ru.excbt.datafuse.nmk.data.model.support;

/**
 *
 * @author kovtonyk
 *
 */
public class CounterInfo {
	private final Long id;
	private final Long count;

	private CounterInfo(Long id, Long count) {
		this.id = id;
		this.count = count;
	}

	public static CounterInfo newInstance(Object id, Object count) {

		if (id instanceof Long && count instanceof Long) {
			return new CounterInfo((Long) id, (Long) count);

		} else if (id instanceof Number && count instanceof Number) {

			long idValue = ((Number) id).longValue();
			long countValue = ((Number) count).longValue();
			return new CounterInfo(idValue, countValue);

		}

		throw new IllegalArgumentException("Can't determine type for CounterInfo arguments ");

	}

	public Long getId() {
		return id;
	}

	public Long getCount() {
		return count;
	}

}
