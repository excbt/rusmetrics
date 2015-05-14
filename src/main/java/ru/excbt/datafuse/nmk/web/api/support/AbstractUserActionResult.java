package ru.excbt.datafuse.nmk.web.api.support;

public abstract class AbstractUserActionResult<T> extends AbstractApiAction {

	protected final T entity;
	private T resultEntity;

	public AbstractUserActionResult(T entity) {
		this.entity = entity;
	}

	protected T getResultEntity() {
		if (resultEntity == null) {
			throw new IllegalStateException("resultEntity is not processed");
		}
		return resultEntity;
	}

	protected void setResultEntity(T resultEntity) {
		this.resultEntity = resultEntity;
	}

	@Override
	public Object getResult() {
		Object result = getResultEntity();
		return result != null ? result : EMPTY_RESULT;
	}

}
