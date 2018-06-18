package ru.excbt.datafuse.nmk.web.rest.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.support.PageInfoList;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.RequestAnyDataSelector;
import ru.excbt.datafuse.nmk.web.api.support.RequestPageDataSelector;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Базовый класс для доступа к данным для абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2015
 *
 */
public class AbstractContServiceDataResource  {

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @param dataDateSort
	 * @param pageable
	 * @param dataSelector
	 * @return
	 */
	public static  <T> ResponseEntity<?> getResponseServiceDataPaged(long contObjectId, long contZPointId,
			String timeDetailType, String fromDateStr, String toDateStr, String dataDateSort, Pageable pageable,
			RequestPageDataSelector<T> dataSelector, ContZPointService contZPointService) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);
		checkNotNull(fromDateStr);
		checkNotNull(toDateStr);

		Direction dataDateDirection = Sort.Direction.DESC;
		if ("asc".equalsIgnoreCase(dataDateSort)) {
			dataDateDirection = Sort.Direction.ASC;
		}

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (!datePeriodParser.isOk()) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters fromDateStr:%s and toDateStr:%s", fromDateStr, toDateStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"Invalid parameters fromDateStr:%s is greater than toDateStr:%s", fromDateStr, toDateStr));
		}

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("contZPointId (id=%d) not found", contZPointId));
		}

		if (contZPoint.getContObject() == null || contZPoint.getContObject().getId() != contObjectId) {
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"contZPointId (id=%d) is not valid for contObject (id=%d)", contZPointId, contObjectId));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return ApiResponse.responseBadRequest(
					ApiResult.validationError("Invalid parameters timeDetailType: %s", timeDetailType));
		}

		Sort sort = new Sort(dataDateDirection, "dataDate");

		PageRequest pageRequest = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Page<T> result = dataSelector.selectData(contZPointId, timeDetail, datePeriodParser.getLocalDatePeriod(),
				pageRequest);

		return ResponseEntity.ok(new PageInfoList<T>(result));

	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fromDateStr
	 * @param toDateStr
	 * @param dataSelector
	 * @return
	 */
	public static <T> ResponseEntity<?> getResponseServiceData(long contObjectId, long contZPointId, String timeDetailType,
			String fromDateStr, String toDateStr, RequestAnyDataSelector<T> dataSelector, ContZPointService contZPointService) {

		checkArgument(contObjectId > 0);
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetailType);
		checkNotNull(fromDateStr);
		checkNotNull(toDateStr);

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (!datePeriodParser.isOk()) {
			return ResponseEntity.badRequest()
					.body(String.format("Invalid parameters fromDateStr:%s and toDateStr:%s", fromDateStr, toDateStr));
		}

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"Invalid parameters fromDateStr:%s is greater than toDateStr:%s", fromDateStr, toDateStr));
		}

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("contZPointId (id=%d) not found", contZPointId));
		}

		if (contZPoint.getContObject() == null || contZPoint.getContObject().getId() != contObjectId) {
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"contZPointId (id=%d) is not valid for contObject (id=%d)", contZPointId, contObjectId));
		}

		TimeDetailKey timeDetail = TimeDetailKey.searchKeyname(timeDetailType);
		if (timeDetail == null) {
			return ApiResponse.responseBadRequest(
					ApiResult.validationError("Invalid parameters timeDetailType: %s", timeDetailType));
		}

		T result = dataSelector.selectData(contZPointId, timeDetail, datePeriodParser.getLocalDatePeriod());

		return ResponseEntity.ok(result);

	}

}
