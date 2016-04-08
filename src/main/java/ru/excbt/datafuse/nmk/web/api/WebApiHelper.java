package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;

import ru.excbt.datafuse.nmk.data.model.support.ModelIsNotValidException;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;

/**
 * Утилита для выполнения запросов контроллеров
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.05.2015
 *
 */
public class WebApiHelper {

	private static final Logger logger = LoggerFactory.getLogger(WebApiHelper.class);

	public static class ProcessResponseHelper<T> {
		private final ResponseEntity<T> response;
		private final T entity;

		private ProcessResponseHelper(T entity, ResponseEntity<T> response) {
			this.entity = entity;
			this.response = response;
		}

		public ResponseEntity<T> getResponse() {
			return response;
		}

		public T getEntity() {
			return entity;
		}

	}

	private WebApiHelper() {

	}

	private static class ApiProcessResult {
		//private ResponseEntity<?> errorResponse;
		private final ApiResult apiErrorResult;

		private boolean isError() {
			return this.apiErrorResult != null;
		}

		public ApiProcessResult(ApiResult apiErrorResult) {
			this.apiErrorResult = apiErrorResult;
		}

		public ApiProcessResult() {
			this.apiErrorResult = null;
		}

		private ResponseEntity<?> buildErrorResponse() {
			checkNotNull(apiErrorResult.getHttpStatus());
			return ResponseEntity.status(apiErrorResult.getHttpStatus()).body(apiErrorResult);
		}
	}

	/**
	 * 
	 * @param service
	 * @param id
	 * @return
	 */
	private static ResponseEntity<?> _processResponceApiAction(ApiAction action, HttpStatus successStatus) {

		checkNotNull(action);

		ApiProcessResult processResult = _internalProcess(action);

		if (processResult.isError()) {
			return processResult.buildErrorResponse();
		}

		if (action.getResult() == null || action.getResult() == ApiActionAdapter.EMPTY_RESULT) {
			return ResponseEntity.status(successStatus).build();
		} else {
			return ResponseEntity.status(successStatus).body(action.getResult());
		}
	}

	/**
	 * 
	 * @param service
	 * @param id
	 * @return
	 */
	private static ResponseEntity<?> _processResponceApiActionBody(ApiAction action, HttpStatus successStatus) {

		checkNotNull(action);
		checkArgument(successStatus != HttpStatus.CREATED, "HttpStatus.CREATED is not supported");

		ApiProcessResult processResult = _internalProcess(action);

		if (processResult.isError()) {
			return processResult.buildErrorResponse();
		}

		if (action.getResult() == null || action.getResult() == ApiActionAdapter.EMPTY_RESULT) {
			return ResponseEntity.status(successStatus).build();
		} else {
			return ResponseEntity.status(successStatus).body(action.getResult());
		}

	}

	/**
	 * 
	 * @param action
	 * @param successStatus
	 * @return
	 */
	private static ApiProcessResult _internalProcess(ApiAction action) {
		checkNotNull(action);

		ApiProcessResult result = null;

		try {
			action.process();
		} catch (AccessDeniedException e) {
			logger.warn("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			result = new ApiProcessResult(ApiResult.error(e, HttpStatus.FORBIDDEN));
		} catch (TransactionSystemException | PersistenceException e) {
			logger.warn("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			result = new ApiProcessResult(ApiResult.error(e, HttpStatus.UNPROCESSABLE_ENTITY));
		} catch (ModelIsNotValidException e) {
			logger.warn("Error during process UserAction:{}. ModelIsNotValidException:{}", action.getClass(), e);
			result = new ApiProcessResult(ApiResult.error(e, HttpStatus.NOT_ACCEPTABLE));
		} catch (Exception e) {
			logger.error("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			result = new ApiProcessResult(ApiResult.error(e, HttpStatus.INTERNAL_SERVER_ERROR));
		}

		if (result != null) {
			return result;
		}

		return new ApiProcessResult();
	}

	/**
	 * 
	 * @param service
	 * @param id
	 * @return
	 */
	public static ResponseEntity<?> processResponceApiActionCreate(ApiActionLocation action) {

		checkNotNull(action);

		ApiProcessResult processResult = _internalProcess(action);

		if (processResult.isError()) {
			return processResult.buildErrorResponse();
		}

		if (action.getResult() == null) {
			return ResponseEntity.created(action.getLocation()).build();
		} else {
			return ResponseEntity.created(action.getLocation()).body(action.getResult());
		}

	}

	/**
	 * 
	 * @param action
	 * @param successStatus
	 * @return
	 */
	public static ResponseEntity<?> processResponceApiActionOkBody(ApiAction action) {
		return _processResponceApiActionBody(action, HttpStatus.OK);
	}

	/**
	 * 
	 * @param action
	 * @param successStatus
	 * @return
	 */
	public static ResponseEntity<?> processResponceApiActionOk(ApiAction action) {
		return _processResponceApiAction(action, HttpStatus.OK);
	}

	/**
	 * 
	 * @param action
	 * @param successStatus
	 * @return
	 */
	public static ResponseEntity<?> processResponceApiActionDelete(ApiAction action) {
		return _processResponceApiAction(action, HttpStatus.NO_CONTENT);
	}

	/**
	 * 
	 * @param action
	 * @param successStatus
	 * @return
	 */
	public static ResponseEntity<?> processResponceApiActionUpdate(ApiAction action) {
		return _processResponceApiActionBody(action, HttpStatus.OK);
	}

}
