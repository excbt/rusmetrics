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
	private final T model;

	public ModelWrapper(T model) {
		checkNotNull(model);
		this.model = model;
	}

	public T getModel() {
		return model;
	}

	/**
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((model == null) ? 0 : model.hashCode());
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
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		return true;
	}

}