package ru.excbt.datafuse.nmk.web.api.support;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.PersistenceException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;

import ru.excbt.datafuse.nmk.data.model.support.ModelIsNotValidException;

/**
 * Коды результатов выполнения запроса
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.05.2015
 *
 */
public enum ApiResultCode {
	OK(true, "OK"),
	ERR_UNCKNOWN(false, "Unknown Error"),
	ERR_BAD_REQUEST(false, "Bad Request"),
	ERR_ACCESS_DENIED(false, "Access Denied"),
	ERR_UNPROCESSABLE_TRANSACTION(false, "Unprocessable Transaction"),
	ERR_DATABASE_ERROR(false, "Database Error"),
	ERR_MODEL_VALIDATION_ERROR(false, "Model Validation Error"),
	ERR_BRM_VALIDATION(false, "Buisiness Rule Validation Error"),
	ERR_VALIDATION(false, "Data Validation Error"),
	ERR_INTERNAL(false, "Internal server error"),
	ERR_INVALID_STATE(false, "Invalid State Error"),
	ERR_USER_ALREADY_EXISTS(false, "User Already Exists");

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

		if (e.getClass().equals(ModelIsNotValidException.class)) {
			result = ERR_MODEL_VALIDATION_ERROR;
		}

		return result;
	}

}
