package ru.excbt.datafuse.nmk.web.api.support;

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

	public static ApiResult ok(String description) {
		ApiResult result = new ApiResult(ApiResultCode.OK, description);
		return result;
	}

	public static ApiResult error(Exception e) {
		ApiResult result = new ApiResult(ApiResultCode.describeException(e),
				null);
		return result;
	}

	public static ApiResult error(Exception e, String description) {
		ApiResult result = new ApiResult(ApiResultCode.describeException(e),
				description);
		return result;
	}

}
