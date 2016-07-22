package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointEx;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointStatInfo;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointVO;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;
import ru.excbt.datafuse.nmk.data.service.ContServiceDataHWaterService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.ContZPointService.ContZPointShortInfo;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с точками учета для абонента
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContZPointController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContZPointController.class);

	@Autowired
	protected ContZPointService contZPointService;

	@Autowired
	protected ContServiceDataHWaterService contServiceDataHWaterService;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPoints(@PathVariable("contObjectId") Long contObjectId) {
		List<ContZPoint> zpList = contZPointService.findContObjectZPoints(contObjectId);
		return responseOK(ObjectFilters.deletedFilter(zpList));
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPointsEx", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointsEx(@PathVariable("contObjectId") Long contObjectId) {
		List<ContZPointEx> zpList = contZPointService.findContObjectZPointsEx(contObjectId);
		return responseOK(ObjectFilters.deletedFilter(zpList));
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPoints/vo", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointsVo(@PathVariable("contObjectId") Long contObjectId) {
		List<ContZPointVO> zpList = contZPointService.selectContObjectZPointsVO(contObjectId);
		return responseOK(ObjectFilters.deletedFilter(zpList));
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPointsStatInfo", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointStatInfo(@PathVariable("contObjectId") Long contObjectId) {
		List<ContZPointStatInfo> resultList = contZPointService.selectContZPointStatInfo(contObjectId);
		return responseOK(resultList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @param id
	 * @param settingMode
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestBody ContZPoint contZPoint) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);
		checkNotNull(contZPoint);

		ContZPoint currentContZPoint = contZPointService.findOne(contZPointId);

		if (currentContZPoint == null || !currentContZPoint.getContObject().getId().equals(contObjectId)) {
			return ResponseEntity.badRequest().build();
		}

		currentContZPoint.setCustomServiceName(contZPoint.getCustomServiceName());

		currentContZPoint.setIsManualLoading(contZPoint.getIsManualLoading());

		ApiAction action = new ApiActionEntityAdapter<ContZPoint>(currentContZPoint) {
			@Override
			public ContZPoint processAndReturnResult() {
				return contZPointService.updateContZPoint(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		ContZPoint currentContZPoint = contZPointService.findOne(contZPointId);

		if (currentContZPoint == null || !currentContZPoint.getContObject().getId().equals(contObjectId)) {
			return responseBadRequest();
		}

		return responseOK(currentContZPoint);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/zpoints", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPoints() {

		List<ContZPoint> contZPoints = subscrContObjectService.selectSubscriberContZPoints(getCurrentSubscriberId());

		return responseOK(contZPoints);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/zpoints/shortInfo", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointsInfo() {

		List<ContZPointShortInfo> contZPoints = subscrContObjectService
				.selectSubscriberContZPointShortInfo(getCurrentSubscriberId());

		return responseOK(contZPoints);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contObjects/contServiceTypes", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContServieTypes() {
		List<ContServiceType> contServiceTypes = contZPointService.selectContServiceTypes();

		return responseOK(contServiceTypes);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPoints/timeDetailLastDate", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointsTimeDetailLastDate(@PathVariable("contObjectId") Long contObjectId) {

		List<Long> selectIds = contZPointService.selectContZPointIds(contObjectId);

		if (selectIds == null || selectIds.size() == 0) {
			return responseOK();
		}

		HashMap<Long, List<TimeDetailLastDate>> result = contServiceDataHWaterService
				.selectTimeDetailLastDateMap(selectIds);
		return responseOK(result);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPoints/{contZPointId}/timeDetailLastDate",
			method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointTimeDetailLastDate(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		List<Long> selectIds = Lists.newArrayList(contZPointId);

		HashMap<Long, List<TimeDetailLastDate>> result = contServiceDataHWaterService
				.selectTimeDetailLastDateMap(selectIds);
		return responseOK(result.get(contZPointId));
	}

}
