package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ModelWrapper<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1121524482713584202L;

	@JsonUnwrapped
	private final T object;

	public ModelWrapper(T srcObject) {
		checkNotNull(srcObject);
		this.object = srcObject;
	}

	public T getObject() {
		return object;
	}

	/**
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		return result;
	}

	/**
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelWrapper<?> other = (ModelWrapper<?>) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}

}