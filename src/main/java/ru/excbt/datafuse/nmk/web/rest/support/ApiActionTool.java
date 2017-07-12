package ru.excbt.datafuse.nmk.web.rest.support;

import ru.excbt.datafuse.nmk.data.domain.ModelIdable;
import ru.excbt.datafuse.nmk.data.model.support.ModelIsNotValidException;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionCallMetrics;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiResultCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;

import javax.persistence.PersistenceException;

import java.io.Serializable;
import java.net.URI;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.*;

/**
 * Утилита для выполнения запросов контроллеров
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.05.2015
 *
 */
public class ApiActionTool {

	private static final Logger logger = LoggerFactory.getLogger(ApiActionTool.class);

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

	private ApiActionTool() {

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
	 *
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since 28.09.2016
	 *
	 * @param <T>
	 */
	private static abstract class ApiActionProcessWrapper<T> implements ApiActionAdapter {

		private T resultEntity;

		private ApiActionProcessWrapper() {
			super();
		}

		@Override
		public void process() {
			resultEntity = processAndReturnResult();
		}

		@Override
		public Object getResult() {
			return resultEntity;
		}

		public abstract T processAndReturnResult();
	}

	/**
	 *
	 *
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since 29.09.2016
	 *
	 * @param <T>
	 * @param <K>
	 */
	private static abstract class ApiActionPersistableProcessWrapper<T extends ModelIdable<K>, K extends Serializable>
			extends ApiActionProcessWrapper<T> implements ApiActionAdapter, ApiActionLocation {

		private ApiActionPersistableProcessWrapper() {
			super();
		}

		protected K getLocationId() {
			return super.resultEntity.getId();
		}

		@Override
		public abstract T processAndReturnResult();
	}

    /**
     *
     * @param action
     * @param successStatus
     * @return
     */
	private static ResponseEntity<?> _processResponceApiAction(ApiAction action, HttpStatus successStatus) {
		return _processResponceApiAction(action, successStatus, null);
	}

	/**
	 *
	 * @param action
	 * @param successStatus
	 * @param extraCheck
	 * @return
	 */
	private static <T> ResponseEntity<?> _processResponceApiAction(ApiAction action, HttpStatus successStatus,
			Function<T, ResponseEntity<?>> extraCheck) {

		checkNotNull(action);
		checkArgument(successStatus != HttpStatus.CREATED, "HttpStatus.CREATED is not supported");

		ApiProcessResult processResult = _internalProcess(action);

		if (processResult.isError()) {
			return processResult.buildErrorResponse();
		}

		if (extraCheck != null) {

			try {
				@SuppressWarnings("unchecked")
				ResponseEntity<?> extraCheckResult = extraCheck.apply((T) action.getResult());
				if (extraCheckResult != null) {
					return extraCheckResult;
				}
			} catch (Exception e) {
				logger.error("Extra Check Type error UserAction:{}. exception:{}", action.getClass(), e);

				ApiProcessResult errorResponse = new ApiProcessResult(ApiResult.error(ApiResultCode.ERR_INTERNAL,
						"Extra Check Type error", HttpStatus.INTERNAL_SERVER_ERROR));

				return errorResponse.buildErrorResponse();
			}
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
	@Deprecated
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
     * @return
     */
	private static ApiProcessResult _internalProcess(ApiAction action) {
		checkNotNull(action);

		ApiProcessResult result = null;

		try {
			final ApiActionCallMetrics callMetrics = ApiActionCallMetrics.newMetrics().start();
			action.process();
			final ApiActionCallMetrics callMetricsResult = callMetrics.end();
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
     * @param action
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
	 * @param uriLocation
	 * @return
	 */
	//	public static <T extends Persistable<Long>> ResponseEntity<?> processResponceApiActionCreateIdLong(
	//			final ApiActionProcess<T> actionProcess, final Supplier<String> uriLocationSupplier) {
	//
	//		checkNotNull(actionProcess);
	//
	//		ApiActionLocationProcessWrapper<T, ?> action = new ApiActionLocationProcessWrapper<T, Long>() {
	//
	//			@Override
	//			public T processAndReturnResult() {
	//				return actionProcess.processAndReturnResult();
	//			}
	//
	//			@Override
	//			public URI getLocation() {
	//				checkNotNull(uriLocationSupplier);
	//				checkNotNull(uriLocationSupplier.get(), "request is NULL");
	//
	//				URI location = null;
	//				if (getResult() != null && getLocationId() != null) {
	//					location = URI.create(uriLocationSupplier.get() + '/' + getLocationId());
	//				} else {
	//					location = URI.create(uriLocationSupplier.get());
	//				}
	//
	//				return location;
	//			}
	//
	//		};
	//
	//		ApiProcessResult processResult = _internalProcess(action);
	//
	//		if (processResult.isError()) {
	//			return processResult.buildErrorResponse();
	//		}
	//
	//		if (action.getResult() == null) {
	//			return ResponseEntity.created(action.getLocation()).build();
	//		} else {
	//			return ResponseEntity.created(action.getLocation()).body(action.getResult());
	//		}
	//
	//	}

	/**
	 *
	 * @param actionProcess
	 * @param uriLocationSupplier
	 * @return
	 */
	public static <T extends ModelIdable<K>, K extends Serializable> ResponseEntity<?> processResponceApiActionCreate(
			final ApiActionProcess<T> actionProcess, final Supplier<String> uriLocationSupplier) {

		checkNotNull(actionProcess);

		ApiActionPersistableProcessWrapper<T, K> action = new ApiActionPersistableProcessWrapper<T, K>() {

			@Override
			public T processAndReturnResult() {
				return actionProcess.processAndReturnResult();
			}

			@Override
			public URI getLocation() {
				checkNotNull(uriLocationSupplier);
				checkNotNull(uriLocationSupplier.get(), "request is NULL");

				URI location = null;
				if (getResult() != null && getLocationId() != null) {
					location = URI.create(uriLocationSupplier.get() + '/' + getLocationId());
				} else {
					location = URI.create(uriLocationSupplier.get());
				}

				return location;
			}

		};

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
     * @return
     */
	@Deprecated
	protected static ResponseEntity<?> processResponceApiActionOkBody(ApiAction action) {
		return _processResponceApiAction(action, HttpStatus.OK);
	}

	/**
	 *
	 * @param actionProcess
	 * @return
	 */
	public static <T> ResponseEntity<?> processResponceApiActionOk(final ApiActionProcess<T> actionProcess) {
		return _processResponceApiAction(createWrapper(actionProcess), HttpStatus.OK);
	}

	/**
	 *
	 * @param actionProcess
	 * @return
	 */
	public static <T> ResponseEntity<?> processResponceApiActionOk(final ApiActionProcess<T> actionProcess,
			Function<T, ResponseEntity<?>> extraCheck) {
		return _processResponceApiAction(createWrapper(actionProcess), HttpStatus.OK, extraCheck);
	}

    /**
     *
     * @param action
     * @return
     */
	public static ResponseEntity<?> processResponceApiActionOk(ApiAction action) {
		return _processResponceApiAction(action, HttpStatus.OK);
	}

    /**
     *
     * @param action
     * @return
     */
	public static ResponseEntity<?> processResponceApiActionDelete(ApiAction action) {
		return _processResponceApiAction(action, HttpStatus.NO_CONTENT);
	}

	/**
	 *
	 * @param actionProcess
	 * @return
	 */
	public static <T> ResponseEntity<?> processResponceApiActionDelete(final ApiActionProcess<T> actionProcess) {
		return _processResponceApiAction(createWrapper(actionProcess), HttpStatus.NO_CONTENT);
	}

	/**
	 *
	 * @param action
	 * @return
	 */
	public static ResponseEntity<?> processResponceApiActionDeleteBody(ApiAction action) {
		return _processResponceApiAction(action, HttpStatus.OK);
	}

    /**
     *
     * @param action
     * @return
     */
	public static ResponseEntity<?> processResponceApiActionUpdate(ApiAction action) {
		return _processResponceApiAction(action, HttpStatus.OK);
	}

	/**
	 *
	 * @param actionProcess
	 * @return
	 */
	public static <T> ResponseEntity<?> processResponceApiActionUpdate(final ApiActionProcess<T> actionProcess) {
		return _processResponceApiAction(createWrapper(actionProcess), HttpStatus.OK);
	}

	/**
	 *
	 * @param actionProcess
	 * @return
	 */
	public static <T> ResponseEntity<?> processResponceApiActionUpdate(final ApiActionProcess<T> actionProcess,
			Function<T, ResponseEntity<?>> extraCheck) {
		return _processResponceApiAction(createWrapper(actionProcess), HttpStatus.OK, extraCheck);
	}

	/**
	 *
	 * @param actionProcess
	 * @return
	 */
	private static <T> ApiActionProcessWrapper<?> createWrapper(final ApiActionProcess<T> actionProcess) {
		final ApiActionProcessWrapper<?> action = new ApiActionProcessWrapper<Object>() {

			@Override
			public Object processAndReturnResult() {
				return actionProcess.processAndReturnResult();
			}

		};
		return action;
	}

}
