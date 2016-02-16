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
public abstract class ApiActionEntityAdapter<T> extends AbstractEntityApiAction<T> {

	public ApiActionEntityAdapter(T entity) {
		super(entity);
	}

	public ApiActionEntityAdapter() {
		super();
	}

	@Override
	public void process() {
		T resultEntity = processAndReturnResult();
		setResultEntity(resultEntity);
	}

	public abstract T processAndReturnResult();
}
