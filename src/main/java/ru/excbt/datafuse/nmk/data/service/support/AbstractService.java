package ru.excbt.datafuse.nmk.data.service.support;

import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Базовый класс для сервисов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.10.2015
 *
 */
public abstract class AbstractService {

	public final static List<Long> NO_DATA_IDS = Collections.unmodifiableList(Arrays.asList(Long.MIN_VALUE));

	@PersistenceContext(unitName = "nmk-p")
	protected EntityManager em;

	/**
	 * 
	 * @param entity
	 * @return
	 */
	protected <T extends DeletableObject> T softDelete(T entity) {
		checkNotNull(entity);
		entity.setDeleted(1);
		return entity;
	}

	/**
	 * 
	 * @param entities
	 * @return
	 */
	protected <T extends DeletableObject> Iterable<T> softDelete(Iterable<T> entities) {
		checkNotNull(entities);
		entities.forEach(i -> {
			i.setDeleted(1);
		});
		return entities;
	}

	/**
	 * 
	 * @param specs
	 * @return
	 */
	protected <T> Specifications<T> specsAndFilterBuild(List<Specification<T>> specList) {
		if (specList == null) {
			return null;
		}
		Specifications<T> result = null;
		for (Specification<T> i : specList) {
			if (i == null) {
				continue;
			}
			result = result == null ? Specifications.where(i) : result.and(i);
		}

		return result;
	}

	/**
	 * 
	 * @param checkIds
	 * @param availableIds
	 * @return
	 */
	public static boolean checkIds(Long[] checkIds, List<Long> availableIds) {

		if (availableIds == null || availableIds.isEmpty()) {
			return false;
		}

		boolean result = true;
		for (Long id : checkIds) {
			result = result && availableIds.contains(id);
		}
		return result;
	}

	/**
	 * 
	 * @param checkIds
	 * @param availableIds
	 * @return
	 */
	public static boolean checkIds(Collection<Long> checkIds, Collection<Long> availableIds) {

		if (availableIds == null || availableIds.isEmpty()) {
			return false;
		}

		boolean result = true;
		for (Long id : checkIds) {
			result = result && availableIds.contains(id);
		}
		return result;
	}

	/**
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 */
	public static <T extends Persistable<?>> PersistenceException entityNotFoundException (Class<T> clazz, Object id) {
		throw new PersistenceException("Entity " + clazz.getSimpleName() + " with ID=" + id + " is not found");
	}
	
}
