package ru.excbt.datafuse.nmk.data.model.support;

import java.util.Optional;

public abstract class MinMaxCheck <T extends Comparable<T>> {

	protected T object;

	protected MinMaxCheck () {
		object = null;
	}

	protected abstract boolean conditionPass(T arg);

	public void check (T arg) {
//		if (arg != null) {
			if (arg != null && (object == null || conditionPass(arg))) {
				object = arg;
			}
//			if (object == null) {
//				object = arg;
//			} else if (conditionPass(arg)) {
//				object = arg;
//			}
//		}
	}

	public T getObject() {
		return object;
	}


	public Optional<T> getValue() {
        return Optional.ofNullable(object);
    }
}
