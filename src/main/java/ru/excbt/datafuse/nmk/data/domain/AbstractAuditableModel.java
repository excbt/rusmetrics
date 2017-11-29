package ru.excbt.datafuse.nmk.data.domain;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Базовый класс модель данных с аудитом
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 06.04.2015
 *
 */
@MappedSuperclass
@Audited
@EntityListeners({ AuditingEntityListener.class })
public abstract class AbstractAuditableModel extends AbstractAuditablePersistenceEntity<Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1889220384079670690L;

}
