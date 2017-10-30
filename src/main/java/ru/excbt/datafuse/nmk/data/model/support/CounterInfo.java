package ru.excbt.datafuse.nmk.data.model.support;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author kovtonyk
 *
 */
public class CounterInfo {

    public enum IdRole {
        CONT_OBJECT,
        CONT_ZPOINT;
    }

    // contObjectId and other...
    private final Long id;
	private final Long count;
	private final IdRole idRole;

	private CounterInfo(Long id, Long count) {
		this.id = id;
		this.count = count;
        this.idRole = IdRole.CONT_OBJECT;
	}

    public CounterInfo(Long id, Long count, IdRole idRole) {
        this.id = id;
        this.count = count;
        this.idRole = idRole;
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

	@JsonIgnore
    public IdRole getIdRole() {
        return idRole;
    }
}
