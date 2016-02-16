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

import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
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

	/**
	 * 
	 * @param service
	 * @param id
	 * @return
	 */
	private static ResponseEntity<?> processResponceApiAction(ApiAction action, HttpStatus successStatus) {

		checkNotNull(action);

		try {
			action.process();
		} catch (AccessDeniedException e) {
			logger.error("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResult.error(e));
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ApiResult.error(e));
		}

		return ResponseEntity.status(successStatus).build();
	}

	/**
	 * 
	 * @param service
	 * @param id
	 * @return
	 */
	private static ResponseEntity<?> processResponceApiActionBody(ApiAction action, HttpStatus successStatus) {

		checkNotNull(action);
		checkArgument(successStatus != HttpStatus.CREATED, "HttpStatus.CREATED is not supported");

		try {
			action.process();
		} catch (AccessDeniedException e) {
			logger.error("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResult.error(e));

		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ApiResult.error(e));

		} catch (Exception e) {
			logger.error("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.error(e));

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
	public static ResponseEntity<?> processResponceApiActionCreate(ApiActionLocation action) {

		checkNotNull(action);

		try {
			action.process();
		} catch (AccessDeniedException e) {
			logger.error("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResult.error(e));
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during process UserAction:{}. exception:{}", action.getClass(), e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ApiResult.error(e));
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
		return processResponceApiActionBody(action, HttpStatus.OK);
	}

	/**
	 * 
	 * @param action
	 * @param successStatus
	 * @return
	 */
	public static ResponseEntity<?> processResponceApiActionOk(ApiAction action) {
		return processResponceApiAction(action, HttpStatus.OK);
	}

	/**
	 * 
	 * @param action
	 * @param successStatus
	 * @return
	 */
	public static ResponseEntity<?> processResponceApiActionDelete(ApiAction action) {
		return processResponceApiAction(action, HttpStatus.NO_CONTENT);
	}

	/**
	 * 
	 * @param action
	 * @param successStatus
	 * @return
	 */
	public static ResponseEntity<?> processResponceApiActionUpdate(ApiAction action) {
		return processResponceApiActionBody(action, HttpStatus.OK);
	}

}
