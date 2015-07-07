package ru.excbt.datafuse.nmk.data.model.support;

public abstract class MinMaxCheck <T extends Comparable<T>> {

	protected T object;
	
	protected MinMaxCheck () {
		object = null;
	}
	
	protected abstract boolean conditionPass(T arg);
	
	public void check (T arg) {
		if (arg != null) {
			if (object == null) {
				object = arg;
			} else if (conditionPass(arg)) {
				object = arg;
			}
		}		
	}

	public T getObject() {
		return object;
	}
}
