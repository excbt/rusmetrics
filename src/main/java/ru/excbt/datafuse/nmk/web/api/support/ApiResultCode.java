package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.AccessDeniedException;

import javax.persistence.PersistenceException;

import org.springframework.transaction.TransactionSystemException;

public enum ApiResultCode {
	OK(true, "OK"), ERR_UNCKNOWN(false, "Unknown Error"), ERR_ACCESS_DENIED(
			false, "Access Denied"), ERR_UNPROCESSABLE_TRANSACTION(false,
			"Unprocessable Transaction"), ERR_DATABASE_ERROR(false,
			"Database Error"), ERR_BRM_VALIDATION(false,
			"Buisiness Rule Validation Error");

	private final boolean ok;

	private final String description;

	private ApiResultCode(boolean ok, String description) {
		this.ok = ok;
		this.description = description;
	}

	public boolean isOk() {
		return ok;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	public static ApiResultCode describeException(Exception e) {

		checkNotNull(e);

		ApiResultCode result = ERR_UNCKNOWN;

		if (e.getClass().equals(AccessDeniedException.class)) {
			result = ERR_ACCESS_DENIED;
		}

		if (e.getClass().equals(TransactionSystemException.class)) {
			result = ERR_UNPROCESSABLE_TRANSACTION;
		}

		if (e.getClass().equals(PersistenceException.class)) {
			result = ERR_DATABASE_ERROR;
		}

		return result;
	}

}
