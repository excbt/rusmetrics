package ru.excbt.datafuse.nmk.data.domain;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.model.V_AuditUser;

/**
 * Базовый класс модель данных с аудитом
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 06.04.2015
 *
 */
@MappedSuperclass
@EntityListeners({ AuditingEntityListener.class })
public abstract class AbstractAuditableModel extends AbstractAuditableEntity<V_AuditUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1889220384079670690L;

}
