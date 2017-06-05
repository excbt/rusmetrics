package ru.excbt.datafuse.nmk.web.rest.support;

import ru.excbt.datafuse.nmk.data.domain.AuditableTools;
import ru.excbt.datafuse.nmk.data.domain.ModelIdable;
import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.service.ReportService;
import ru.excbt.datafuse.nmk.web.api.WebApiHelper;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Auditable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.*;

/**
 * Базовый класс для контроллера
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.04.2015
 *
 */
public abstract class AbstractApiResource {

	public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(ReportService.DATE_TEMPLATE);


	@Autowired
	protected ModelMapper modelMapper;

	/**
	 *
	 * @param currentEntity
	 * @param newEntity
	 */
	protected void prepareAuditableProps(Auditable<V_AuditUser, ?> currentEntity, Auditable<V_AuditUser, ?> newEntity) {
		AuditableTools.copyAuditableProps(currentEntity, newEntity);
	}

	/**
	 *
	 * @return
	 */
	protected ResponseEntity<?> responseForbidden() {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResult.build(ApiResultCode.ERR_ACCESS_DENIED));
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
	 * @return
	 */
	protected ResponseEntity<?> responseOK() {
		return ResponseEntity.ok().build();
	}

	/**
	 *
	 * @return
	 */
	protected <T> ResponseEntity<?> responseOK(final ApiActionProcess<T> actionProcess) {
		return WebApiHelper.processResponceApiActionOk(actionProcess);
	}

	/**
	 *
	 * @param actionProcess
	 * @param extraCheck
	 * @return
	 */
	protected <T> ResponseEntity<?> responseOK(final ApiActionProcess<T> actionProcess,
			Function<T, ResponseEntity<?>> extraCheck) {
		return WebApiHelper.processResponceApiActionOk(actionProcess, extraCheck);
	}

	/**
	 *
	 * @return
	 */
	protected ResponseEntity<?> responseOK(Object body) {
		return ResponseEntity.ok(body);
	}

	/**
	 *
	 * @param apiResult
	 * @return
	 */
	protected ResponseEntity<?> responseOK(ApiResult apiResult) {
		return ResponseEntity.status(HttpStatus.OK).body(apiResult);
	}

	/**
	 *
	 * @param actionProcess
	 * @return
	 */
	protected <T> ResponseEntity<?> responseUpdate(final ApiActionProcess<T> actionProcess) {
		return WebApiHelper.processResponceApiActionUpdate(actionProcess);
	}

	/**
	 *
	 * @param actionProcess
	 * @param extraCheck
	 * @return
	 */
	protected <T> ResponseEntity<?> responseUpdate(final ApiActionProcess<T> actionProcess,
			Function<T, ResponseEntity<?>> extraCheck) {
		return WebApiHelper.processResponceApiActionUpdate(actionProcess, extraCheck);
	}

	/**
	 *
	 * @param actionProcess
	 * @return
	 */
	protected <T extends ModelIdable<K>, K extends Serializable> ResponseEntity<?> responseCreate(
			final ApiActionProcess<T> actionProcess, final Supplier<String> uriLocationSupplier) {
		return WebApiHelper.processResponceApiActionCreate(actionProcess, uriLocationSupplier);

	}

	/**
	 *
	 * @return
	 */
	protected <T> ResponseEntity<?> responseDelete(final ApiActionProcess<T> actionProcess) {
		return WebApiHelper.processResponceApiActionDelete(actionProcess);
	}

	/**
	 *
	 * @param apiResult
	 * @return
	 */
	protected ResponseEntity<?> responseBadRequest(ApiResult apiResult) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResult);
	}

	/**
	 *
	 * @param apiResult
	 * @return
	 */
	protected ResponseEntity<?> responseInternalServerError(ApiResult apiResult) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResult);
	}

    /**
     *
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
	protected ResponseEntity<?> processDownloadInputStream(InputStream is, MediaType mediaType, long contentLength,
			String filename) {
		checkNotNull(is);

		InputStreamResource isr = new InputStreamResource(is);
		return processDownloadResource(isr, mediaType, contentLength, filename);
	}

	/**
	 *
	 * @param is
	 * @param mediaType
	 * @param contentLength
	 * @param filename
	 * @param makeAttach
	 * @return
	 */
	protected ResponseEntity<?> processDownloadInputStream(InputStream is, MediaType mediaType, long contentLength,
			String filename, boolean makeAttach) {
		checkNotNull(is);

		InputStreamResource isr = new InputStreamResource(is);
		return processDownloadResource(isr, mediaType, contentLength, filename, makeAttach);
	}

    /**
     *
     * @param file
     * @param mediaType
     * @return
     */
	protected ResponseEntity<?> processDownloadFile(File file, MediaType mediaType) {
		checkNotNull(file);
		FileSystemResource fsr = new FileSystemResource(file);
		return processDownloadResource(fsr, mediaType, file.length(), file.getName());
	}

	/**
	 *
	 * @param resource
	 * @param mediaType
	 * @param contentLength
	 * @param filename
	 * @return
	 */
	protected ResponseEntity<?> processDownloadResource(Resource resource, MediaType mediaType, long contentLength,
			String filename, boolean makeAttach) {

		checkNotNull(resource);
		checkNotNull(mediaType);
		checkNotNull(filename);

		// set content attributes for the response
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		headers.setContentLength(contentLength);

		if (makeAttach) {
			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", filename);
			headers.set(headerKey, headerValue);
		}

		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}

    /**
     *
     * @param datePeriodParser
     * @return
     */
	protected ResponseEntity<?> checkDatePeriodArguments(LocalDatePeriodParser datePeriodParser) {
		if (!datePeriodParser.isOk()) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters dateFrom:{} and dateTo:{}",
							datePeriodParser.getParserArguments().dateFromStr,
							datePeriodParser.getParserArguments().dateToStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters dateFrom:{} is greater than dateTo:{}",
							datePeriodParser.getParserArguments().dateFromStr,
							datePeriodParser.getParserArguments().dateToStr));
		}

		return null;
	}

	/**
	 *
	 * @param resource
	 * @param mediaType
	 * @param contentLength
	 * @param filename
	 * @return
	 */
	protected ResponseEntity<?> processDownloadResource(Resource resource, MediaType mediaType, long contentLength,
			String filename) {

		return processDownloadResource(resource, mediaType, contentLength, filename, true);
	}

	/**
	 *
	 * @param srcList
	 * @param destClass
	 * @return
	 */
	protected <S, M> List<M> makeModelMapper(Collection<S> srcList, Class<M> destClass) {
		checkNotNull(srcList);
		return srcList.stream().map((i) -> modelMapper.map(i, destClass)).collect(Collectors.toList());
	}

	/**
	 *
	 * @param srcStream
	 * @param destClass
	 * @return
	 */
	protected <S, M> List<M> makeModelMapper(Stream<S> srcStream, Class<M> destClass) {
		checkNotNull(srcStream);
		return srcStream.map((i) -> modelMapper.map(i, destClass)).collect(Collectors.toList());
	}

}
