package ru.excbt.datafuse.nmk.data.domain;

import javax.persistence.MappedSuperclass;

import ru.excbt.datafuse.nmk.data.model.AuditUser;

@MappedSuperclass
public abstract class AbstractAuditableModel extends AbstractAuditableEntity<AuditUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1889220384079670690L;

}
