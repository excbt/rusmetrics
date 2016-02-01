package ru.excbt.datafuse.nmk.web.api.support;

/**
 * Адаптер для AbstractEntityApiAction
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.10.2015
 * 
 * @param <T>
 */
public abstract class EntityApiActionAdapter<T> extends AbstractEntityApiAction<T> {

	public EntityApiActionAdapter(T entity) {
		super(entity);
	}

	public EntityApiActionAdapter() {
		super();
	}

	@Override
	public void process() {
		T resultEntity = processAndReturnResult();
		setResultEntity(resultEntity);
	}

	public abstract T processAndReturnResult();
}
