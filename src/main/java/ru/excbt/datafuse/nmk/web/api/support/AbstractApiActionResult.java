package ru.excbt.datafuse.nmk.web.api.support;

public abstract class AbstractApiActionResult<T> extends AbstractApiAction {

	protected final T entity;
	private T resultEntity;

	public AbstractApiActionResult(T entity) {
		this.entity = entity;
	}

	public AbstractApiActionResult() {
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
