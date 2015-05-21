package ru.excbt.datafuse.nmk.web.api.support;

public abstract class AbstractEntityApiAction<T> extends AbstractApiAction {

	protected final T entity;
	private T resultEntity;

	public AbstractEntityApiAction(T entity) {
		this.entity = entity;
	}

	public AbstractEntityApiAction() {
		this.entity = null;
	}

	protected T getResultEntity() {
		return resultEntity;
	}

	protected void setResultEntity(T resultEntity) {
		this.resultEntity = resultEntity;
	}

	@Override
	public Object getResult() {
		return getResultEntity();
	}

}
