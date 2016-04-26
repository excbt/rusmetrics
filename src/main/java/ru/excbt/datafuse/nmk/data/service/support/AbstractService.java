package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

/**
 * Базовый класс для сервисов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.10.2015
 *
 */
public abstract class AbstractService {

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
	protected boolean checkIds(Long[] checkIds, List<Long> availableIds) {

		if (availableIds == null || availableIds.isEmpty()) {
			return false;
		}

		boolean result = true;
		for (Long id : checkIds) {
			result = result && availableIds.contains(id);
		}
		return result;
	}

}
