package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

public class MaxCheck<T extends Comparable<T>> extends MinMaxCheck<T> {

	@Override
	protected boolean conditionPass(T arg) {
		checkNotNull(arg);
		return object.compareTo(arg) > 0;
	}
}
