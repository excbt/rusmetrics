package ru.excbt.datafuse.nmk.web.api.support;

/**
 * Расширенный класс для работы action с сущностью
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.05.2015
 * 
 * @param <T>
 */
public abstract class AbstractEntityApiAction<T> implements ApiActionAdapter {

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
