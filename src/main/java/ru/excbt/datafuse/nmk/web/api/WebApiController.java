package ru.excbt.datafuse.nmk.web.api;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.domain.Auditable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ru.excbt.datafuse.nmk.data.domain.AuditableTools;
import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;

public class WebApiController {

	public static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

	public static final int DEFAULT_PAGE_SIZE = 100;

	public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern(ReportService.DATE_TEMPLATE);

	public final static String MIME_ZIP = "application/zip";
	public final static String MIME_PDF = "application/pdf";
	public final static String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public final static String MIME_XLS = "application/vnd.ms-excel";

	public final static String MIME_CSV = "text/csv";
	public final static String MIME_TEXT = "text/html";

	public final static String FILE_CSV_EXT = ".csv";

	/**
	 * 
	 * @param currentEntity
	 * @param newEntity
	 */
	protected void prepareAuditableProps(Auditable<AuditUser, ?> currentEntity,
			Auditable<AuditUser, ?> newEntity) {
		AuditableTools.copyAuditableProps(currentEntity, newEntity);
	}

	/**
	 * 
	 * @return
	 */
	protected ResponseEntity<?> responseForbidden() {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
				ApiResult.build(ApiResultCode.ERR_ACCESS_DENIED));
	}

	/**
	 * 
	 * @return
	 */
	protected ResponseEntity<?> responseNotFound() {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	/**
	 * 
	 * @return
	 */
	protected ResponseEntity<?> responseNoContent() {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	/**
	 * 
	 * @return
	 */
	protected ResponseEntity<?> responseBadRequest() {
		return ResponseEntity.badRequest().build();
	}

}
