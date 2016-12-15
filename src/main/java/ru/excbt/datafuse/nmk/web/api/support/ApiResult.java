package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Результат REST запроса
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.05.2015
 *
 */
@JsonInclude(value = Include.NON_NULL)
public class ApiResult {

	private final ApiResultCode resultCode;
	private final String description;
	private final HttpStatus httpStatus;
	private final List<String> descriptionLines;

	public ApiResult(ApiResultCode resultCode, String description) {
		this.resultCode = resultCode;
		this.description = description;
		this.httpStatus = null;
		this.descriptionLines = null;
	}

	public ApiResult(ApiResultCode resultCode, List<String> descriptionLines) {
		this.resultCode = resultCode;
		this.description = null;
		this.httpStatus = null;
		this.descriptionLines = descriptionLines != null ? new ArrayList<>(descriptionLines) : null;
	}

	public ApiResult(ApiResultCode resultCode, String description, HttpStatus httpStatus) {
		this.resultCode = resultCode;
		this.description = description;
		this.httpStatus = httpStatus;
		this.descriptionLines = null;
	}

	public ApiResult(ApiResultCode resultCode) {
		this.resultCode = resultCode;
		this.description = null;
		this.httpStatus = null;
		this.descriptionLines = null;
	}

	public ApiResultCode getResultCode() {
		return resultCode;
	}

	public String getDescription() {
		return description;
	}

	public boolean isSuccess() {
		return resultCode.isOk();
	}

	/**
	 * 
	 * @param code
	 * @return
	 */
	public static ApiResult build(ApiResultCode code) {
		checkNotNull(code);
		return new ApiResult(code, null, null);
	}

	/**
	 * 
	 * @param code
	 * @param description
	 * @return
	 */
	public static ApiResult build(ApiResultCode code, String description) {
		checkNotNull(code);
		return new ApiResult(code, description);
	}

	/**
	 * 
	 * @param code
	 * @param description
	 * @param httpStatus
	 * @return
	 */
	public static ApiResult build(ApiResultCode code, String description, HttpStatus httpStatus) {
		checkNotNull(code);
		return new ApiResult(code, description, httpStatus);
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public static ApiResult ok(String description) {
		return new ApiResult(ApiResultCode.OK, description);
	}

	/**
	 * 
	 * @return
	 */
	public static ApiResult ok() {
		return new ApiResult(ApiResultCode.OK, null, null);
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public static ApiResult validationError(String description) {
		return new ApiResult(ApiResultCode.ERR_VALIDATION, description);
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public static ApiResult badRequest(String description) {
		return new ApiResult(ApiResultCode.ERR_BAD_REQUEST, description);
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public static ApiResult badRequest(List<String> descriptionLines) {
		return new ApiResult(ApiResultCode.ERR_BAD_REQUEST, descriptionLines);
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public static ApiResult badRequest(String description, Object... args) {
		return build(ApiResultCode.ERR_BAD_REQUEST, String.format(description, args));
	}

	/**
	 * 
	 * @param description
	 * @param args
	 * @return
	 */
	public static ApiResult validationError(String description, Object... args) {
		return new ApiResult(ApiResultCode.ERR_VALIDATION, String.format(description, args));
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	public static ApiResult error(Exception e) {
		return new ApiResult(ApiResultCode.describeException(e), e.getMessage());
	}

	/**
	 * 
	 * @param e
	 * @param httpStatus
	 * @return
	 */
	public static ApiResult error(Exception e, HttpStatus httpStatus) {
		return new ApiResult(ApiResultCode.describeException(e), e.getMessage(), httpStatus);
	}

	/**
	 * 
	 * @param description
	 * @param httpStatus
	 * @return
	 */
	public static ApiResult error(ApiResultCode apiResultCode, String description, HttpStatus httpStatus) {
		return new ApiResult(apiResultCode, description, httpStatus);
	}

	/**
	 * 
	 * @param e
	 * @param description
	 * @return
	 */
	public static ApiResult error(Exception e, String description) {
		return new ApiResult(ApiResultCode.describeException(e), description);
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	public static ApiResult internalError(String description) {
		return new ApiResult(ApiResultCode.ERR_INTERNAL, description);
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public static ApiResult invalidState(String description) {
		return new ApiResult(ApiResultCode.ERR_INVALID_STATE, description);
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public List<String> getDescriptionLines() {
		return descriptionLines;
	}

}
