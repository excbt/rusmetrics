package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.InputStream;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Auditable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

	/**
	 * 
	 * @param apiResult
	 * @return
	 */
	protected ResponseEntity<?> responseBadRequest(ApiResult apiResult) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				apiResult);
	}

	/**
	 * 
	 * @param apiResult
	 * @return
	 */
	protected ResponseEntity<?> responseInternalServerError(ApiResult apiResult) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				apiResult);
	}

	/**
	 * 
	 * @param apiResult
	 * @return
	 */
	protected ResponseEntity<?> responseInternalServerError() {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/**
	 * 
	 * @param is
	 * @param mediaType
	 * @param contentLength
	 * @param filename
	 * @return
	 */
	protected ResponseEntity<?> processDownloadInputStream(InputStream is,
			MediaType mediaType, long contentLength, String filename) {
		checkNotNull(is);

		InputStreamResource isr = new InputStreamResource(is);
		return processDownloadResource(isr, mediaType, contentLength, filename);
	}

	/**
	 * 
	 * @param resource
	 * @param mediaType
	 * @param file
	 * @return
	 */
	protected ResponseEntity<?> processDownloadFile(File file,
			MediaType mediaType) {
		checkNotNull(file);
		FileSystemResource fsr = new FileSystemResource(file);
		return processDownloadResource(fsr, mediaType, file.length(),
				file.getName());
	}

	/**
	 * 
	 * @param resource
	 * @param mediaType
	 * @param contentLength
	 * @param filename
	 * @return
	 */
	protected ResponseEntity<?> processDownloadResource(Resource resource,
			MediaType mediaType, long contentLength, String filename) {

		checkNotNull(resource);
		checkNotNull(mediaType);
		checkNotNull(filename);

		// set content attributes for the response
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		headers.setContentLength(contentLength);

		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				filename);
		headers.set(headerKey, headerValue);

		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}

}
