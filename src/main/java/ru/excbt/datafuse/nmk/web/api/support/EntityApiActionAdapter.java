package ru.excbt.datafuse.nmk.web.api.support;

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
