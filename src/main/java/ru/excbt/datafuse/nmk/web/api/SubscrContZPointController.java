package ru.excbt.datafuse.nmk.web.api;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.data.model.support.*;
import ru.excbt.datafuse.nmk.data.model.vo.ContZPointVO;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.service.ContZPointService.ContZPointShortInfo;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

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
public class SubscrContZPointController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContZPointController.class);

	@Autowired
	protected ContZPointService contZPointService;

	@Autowired
	protected ContServiceDataHWaterService contServiceDataHWaterService;

	@Autowired
	protected ContServiceDataElService contServiceDataElService;

	@Autowired
	protected ContZPointMetadataService contZPointMetadataService;

	@Autowired
	protected MeasureUnitService measureUnitService;

	@Autowired
	protected OrganizationService organizationService;

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
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
			produces = ApiConst.APPLICATION_JSON_UTF8)
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
			produces = ApiConst.APPLICATION_JSON_UTF8)
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
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointStatInfo(@PathVariable("contObjectId") Long contObjectId) {
		List<ContZPointStatInfo> resultList = contZPointService.selectContZPointStatInfo(contObjectId);
		return responseOK(resultList);
	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @param contZPoint
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
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
			produces = ApiConst.APPLICATION_JSON_UTF8)
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
	@RequestMapping(value = "/contObjects/zpoints", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPoints() {

		List<ContZPoint> contZPoints = subscrContObjectService.selectSubscriberContZPoints(getCurrentSubscriberId());

		return responseOK(contZPoints);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/zpoints/shortInfo", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
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
			produces = ApiConst.APPLICATION_JSON_UTF8)
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
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointsTimeDetailLastDate(@PathVariable("contObjectId") Long contObjectId) {

		List<Pair<String, Long>> idPairList = contZPointService.selectContZPointServiceTypeIds(contObjectId);

		if (idPairList == null || idPairList.size() == 0) {
			return responseOK();
		}

		HashMap<Long, List<TimeDetailLastDate>> resultHWater = contServiceDataHWaterService
				.selectTimeDetailLastDateMapByPair(idPairList);

		HashMap<Long, List<TimeDetailLastDate>> resultEl = contServiceDataElService
				.selectTimeDetailLastDateMapByPair(idPairList);

		return responseOK(ImmutableMap.builder().putAll(resultHWater).putAll(resultEl).build());
	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPoints/{contZPointId}/timeDetailLastDate",
			method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointTimeDetailLastDate(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		List<Pair<String, Long>> idPairAllList = contZPointService.selectContZPointServiceTypeIds(contObjectId);

		List<Pair<String, Long>> idPairList = idPairAllList.stream().filter(i -> i.getRight().equals(contZPointId))
				.collect(Collectors.toList());

		HashMap<Long, List<TimeDetailLastDate>> resultHWater = contServiceDataHWaterService
				.selectTimeDetailLastDateMapByPair(idPairList);

		HashMap<Long, List<TimeDetailLastDate>> resultEl = contServiceDataElService
				.selectTimeDetailLastDateMapByPair(idPairList);

		return responseOK(ImmutableMap.builder().putAll(resultHWater).putAll(resultEl).build());

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/rsoOrganizations", method = RequestMethod.GET)
	public ResponseEntity<?> getRsoOrganizations(
			@RequestParam(value = "organizationId", required = false) Long organizationId) {
		List<Organization> rsOrganizations = organizationService.selectRsoOrganizations(getSubscriberParam());
		List<Organization> resultList = currentSubscriberService.isSystemUser() ? rsOrganizations
				: ObjectFilters.devModeFilter(rsOrganizations);

		organizationService.checkAndEnhanceOrganizations(resultList, organizationId);

		return responseOK(resultList);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointMetadata(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			responseForbidden();
		}

		List<ContZPointMetadata> result = contZPointMetadataService.selectContZPointMetadata(contZPointId);

		if (result == null || result.isEmpty()) {
			result = contZPointMetadataService.selectNewMetadata(contZPointId, true);
		}

		return responseOK(result);

	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata/measureUnits",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointMetadatameasureUnits(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId,
			@RequestParam(value = "measureUnit", required = false) String measureUnit) {

		List<MeasureUnit> resultList = null;
		if (measureUnit != null) {
			resultList = measureUnitService.selectMeasureUnitsSame(measureUnit);
		} else {
			resultList = measureUnitService.selectMeasureUnits();
		}

		return responseOK(resultList);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata/srcProp",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointMetadataSrcProp(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			responseForbidden();
		}

		List<ContZPointMetadata> metadataList = contZPointMetadataService.selectNewMetadata(contZPointId, false);

		List<DeviceMetadataInfo> result = contZPointMetadataService.buildSrcPropsDeviceMapping(metadataList);

		return responseOK(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata/destProp",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointMetadataDestProp(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		List<ContZPointMetadata> metadataList = contZPointMetadataService.selectNewMetadata(contZPointId, false);

		List<EntityColumn> result = contZPointMetadataService.buildDestProps(metadataList);

		return responseOK(result);
	}

	/**
	 *
	 * @param contObjectId
	 * @param contZPointId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}/metadata/destDb",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointMetadataDestDB(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId) {

		checkNotNull(contObjectId);
		checkNotNull(contZPointId);

		if (!canAccessContObject(contObjectId)) {
			return responseForbidden();
		}

		List<EntityColumn> result = contZPointMetadataService.selectContZPointDestDB(contZPointId);

		return responseOK(result);
	}

}
