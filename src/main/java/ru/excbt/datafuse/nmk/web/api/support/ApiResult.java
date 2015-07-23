package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;

public class ApiResult {

	private final ApiResultCode resultCode;
	private final String description;

	public ApiResult(ApiResultCode resultCode, String description) {
		this.resultCode = resultCode;
		this.description = description;
	}

	public ApiResult(ApiResultCode resultCode) {
		this.resultCode = resultCode;
		this.description = null;
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
		return new ApiResult(code, code.getDescription());
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
	 * @param description
	 * @return
	 */
	public static ApiResult ok(String description) {
		return build(ApiResultCode.OK, description);
	}

	/**
	 * 
	 * @return
	 */
	public static ApiResult ok() {
		return build(ApiResultCode.OK, null);
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public static ApiResult validationError(String description) {
		return build(ApiResultCode.ERR_VALIDATION, description);
	}

	/**
	 * 
	 * @param description
	 * @param args
	 * @return
	 */
	public static ApiResult validationError(String description, Object... args) {
		return build(ApiResultCode.ERR_VALIDATION,
				String.format(description, args));
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	public static ApiResult error(Exception e) {
		return build(ApiResultCode.describeException(e));
	}

	/**
	 * 
	 * @param e
	 * @param description
	 * @return
	 */
	public static ApiResult error(Exception e, String description) {
		return build(ApiResultCode.describeException(e), description);
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	public static ApiResult internalError(String description) {
		return build(ApiResultCode.ERR_INTERNAL, description);
	}
	
	
}
