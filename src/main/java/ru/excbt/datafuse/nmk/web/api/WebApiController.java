package ru.excbt.datafuse.nmk.web.api;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.domain.Auditable;

import ru.excbt.datafuse.nmk.data.domain.AuditableTools;
import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.ReportService;

public class WebApiController {

	public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";
	
	public static final int DEFAULT_PAGE_SIZE = 100;
	
	public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(ReportService.DATE_TEMPLATE);
	
	
	/**
	 * 
	 * @param currentEntity
	 * @param newEntity
	 */
	protected void prepareAuditableProps (Auditable<AuditUser, ?> currentEntity,
			Auditable<AuditUser, ?> newEntity) {
		AuditableTools.copyAuditableProps(currentEntity, newEntity);
	}
}
