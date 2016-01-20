package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.service.OrganizationService;

public abstract class AbstractService {

	@Autowired
	protected OrganizationService organizationService;

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
	 * @param organizationId
	 * @return
	 */
	protected Organization findOrganization(Long organizationId) {
		return checkNotNull(organizationService.findOne(organizationId),
				String.format("Organization (id=%d) is not found", organizationId));
	}

}
