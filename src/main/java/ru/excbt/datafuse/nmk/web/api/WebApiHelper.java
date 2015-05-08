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

import ru.excbt.datafuse.nmk.web.api.support.UserAction;
import ru.excbt.datafuse.nmk.web.api.support.UserActionLocation;

public class WebApiHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(WebApiHelper.class);

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
	public static <T> ResponseEntity<?> processResponceUserAction(
			UserAction userAction, HttpStatus successStatus) {

		checkNotNull(userAction);

		try {
			userAction.process();
		} catch (AccessDeniedException e) {
			logger.error("Error during process UserAction: {}", e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during process UserAction: {}", e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.status(successStatus).build();
	}

	/**
	 * 
	 * @param service
	 * @param id
	 * @return
	 */
	public static <T> ResponseEntity<?> processResponceUserActionUpdate(
			UserAction userAction, HttpStatus successStatus) {

		checkNotNull(userAction);
		checkArgument(successStatus != HttpStatus.CREATED,
				"HttpStatus.CREATED is not supported");

		try {
			userAction.process();
		} catch (AccessDeniedException e) {
			logger.error("Error during process UserAction: {}", e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during process UserAction: {}", e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.status(successStatus)
				.body(userAction.getResult());
	}

	/**
	 * 
	 * @param service
	 * @param id
	 * @return
	 */
	public static <T> ResponseEntity<?> processResponceUserActionCreate(
			UserActionLocation userAction) {

		checkNotNull(userAction);

		try {
			userAction.process();
		} catch (AccessDeniedException e) {
			logger.error("Error during process UserAction: {}", e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during process UserAction: {}", e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.created(userAction.getLocation()).body(
				userAction.getResult());
	}

}
