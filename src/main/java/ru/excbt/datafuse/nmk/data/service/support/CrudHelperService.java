package ru.excbt.datafuse.nmk.data.service.support;

import java.util.List;


public interface CrudHelperService<T> {
	public T createOne(T entity);

	public T updateOne(T entity);

	public T findOne(long id);

	public void deleteOne(long id);

	public void deleteOne(T entity);
	
	public List<T> findAll(long subscriberId);
}
