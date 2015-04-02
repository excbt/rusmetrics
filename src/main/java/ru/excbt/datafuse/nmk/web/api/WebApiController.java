package ru.excbt.datafuse.nmk.web.api;

import org.springframework.data.domain.Auditable;

import ru.excbt.datafuse.nmk.data.domain.AuditableTools;
import ru.excbt.datafuse.nmk.data.model.AuditUser;

public class WebApiController {

	public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";	
	
	/**
	 * 
	 * @param currentEntity
	 * @param savedEntity
	 */
	protected void prepareAuditableProps (Auditable<AuditUser, ?> currentEntity,
			Auditable<AuditUser, ?> savedEntity) {
		AuditableTools.copyAuditableProps(currentEntity, savedEntity);
	}
}
