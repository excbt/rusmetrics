package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectPkeWarn;
import ru.excbt.datafuse.nmk.data.model.keyname.DeviceObjectPkeType;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectPkeService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectPkeService.PkeWarnSearchConditions;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Controller
@RequestMapping(value = "/api/subscr/deviceObjects/pke")
public class SubscrDeviceObjectPkeController {

	protected final DeviceObjectPkeService deviceObjectPkeService;

	protected final ContZPointService contZPointService;

	private final ObjectAccessService objectAccessService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrDeviceObjectPkeController(DeviceObjectPkeService deviceObjectPkeService, ContZPointService contZPointService, ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService) {
        this.deviceObjectPkeService = deviceObjectPkeService;
        this.contZPointService = contZPointService;
        this.objectAccessService = objectAccessService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @param deviceObjectId
	 * @param beginDateStr
	 * @param endDateStr
	 * @return
	 */
	@RequestMapping(value = "/{deviceObjectId}/warn", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectPkeWarn(@PathVariable("deviceObjectId") Long deviceObjectId,
			@RequestParam("beginDate") String beginDateStr, @RequestParam("endDate") String endDateStr,
			@RequestParam(value = "pkeTypeKeynames", required = false) String[] pkeTypeKeynames) {

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(beginDateStr, endDateStr);

		checkNotNull(datePeriodParser);

		ResponseEntity<?> checkPeriod = ApiResponse.checkDatePeriodArguments(datePeriodParser);
		if (checkPeriod != null) {
			return checkPeriod;
		}

		PkeWarnSearchConditions searchConditions = new PkeWarnSearchConditions(deviceObjectId,
				datePeriodParser.getLocalDatePeriod().buildEndOfDay());
		searchConditions.initPkeTypeKeynames(pkeTypeKeynames);

		List<DeviceObjectPkeWarn> resultList = deviceObjectPkeService.selectDeviceObjectPkeWarn(searchConditions);

		return ApiResponse.responseOK(resultList);
	}

	/**
	 *
	 * @param contZPointId
	 * @param beginDateStr
	 * @param endDateStr
	 * @param pkeTypeKeynames
	 * @return
	 */
	@RequestMapping(value = "/byContZPoint/{contZPointId}/warn", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPOintObjectPkeWarn(@PathVariable("contZPointId") Long contZPointId,
			@RequestParam("beginDate") String beginDateStr, @RequestParam("endDate") String endDateStr,
			@RequestParam(value = "pkeTypeKeynames", required = false) String[] pkeTypeKeynames) {

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);
		if (contZPoint == null) {
			return ApiResponse.responseBadRequest();
		}

		if (!objectAccessService.checkContObjectId(contZPoint.getContObjectId(), portalUserIdsService.getCurrentIds())) {
			//return responseForbidden();
		}

		List<Long> deviceObjectIds = contZPointService.selectDeviceObjectIds(contZPointId);
		if (deviceObjectIds.size() != 1) {
			return ApiResponse.responseInternalServerError(ApiResult.internalError("Invalid deviceObject and contZPoint link"));
		}

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(beginDateStr, endDateStr);

		checkNotNull(datePeriodParser);

		ResponseEntity<?> checkPeriod = ApiResponse.checkDatePeriodArguments(datePeriodParser);
		if (checkPeriod != null) {
			return checkPeriod;
		}

		PkeWarnSearchConditions searchConditions = new PkeWarnSearchConditions(deviceObjectIds.get(0),
				datePeriodParser.getLocalDatePeriod().buildEndOfDay());
		searchConditions.initPkeTypeKeynames(pkeTypeKeynames);

		List<DeviceObjectPkeWarn> resultList = deviceObjectPkeService.selectDeviceObjectPkeWarn(searchConditions);

		return ApiResponse.responseOK(resultList);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/types", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getDeviceObjectPkeType() {
		List<DeviceObjectPkeType> resultList = deviceObjectPkeService.selectDeviceObjectPkeType();
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}

}
