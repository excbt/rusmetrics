package ru.excbt.datafuse.nmk.data.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.data.domain.Auditable;

import ru.excbt.datafuse.nmk.data.model.AuditUser;

/**
 * Инструменты для работы с полями аудита
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.04.2015
 *
 */
public class AuditableTools {
	private AuditableTools() {
	}

	/**
	 * 
	 * @param src
	 * @param dest
	 */
	public static void copyAuditableProps(Auditable<AuditUser, ?> src, Auditable<AuditUser, ?> dest) {

		checkNotNull(src);
		checkNotNull(dest);

		dest.setCreatedBy(src.getCreatedBy());
		dest.setCreatedDate(src.getCreatedDate());
		dest.setLastModifiedBy(src.getLastModifiedBy());
		dest.setLastModifiedDate(src.getLastModifiedDate());
	}

}
