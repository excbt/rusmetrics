package ru.excbt.datafuse.nmk.web.rest;

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
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointFullVM;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;
import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;
import ru.excbt.datafuse.nmk.data.model.support.*;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.security.SecurityUtils;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

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
public class SubscrContZPointResource {

	private static final Logger log = LoggerFactory.getLogger(SubscrContZPointResource.class);

	protected final ContZPointService contZPointService;

	protected final ContServiceDataHWaterService contServiceDataHWaterService;

	protected final ContServiceDataElService contServiceDataElService;

	protected final ContZPointMetadataService contZPointMetadataService;

	protected final MeasureUnitService measureUnitService;

	protected final OrganizationService organizationService;

	protected final ObjectAccessService objectAccessService;

    protected final PortalUserIdsService portalUserIdsService;


    @Autowired
    public SubscrContZPointResource(ContZPointService contZPointService, ContServiceDataHWaterService contServiceDataHWaterService, ContServiceDataElService contServiceDataElService, ContZPointMetadataService contZPointMetadataService, MeasureUnitService measureUnitService, OrganizationService organizationService, ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService) {
        this.contZPointService = contZPointService;
        this.contServiceDataHWaterService = contServiceDataHWaterService;
        this.contServiceDataElService = contServiceDataElService;
        this.contZPointMetadataService = contZPointMetadataService;
        this.measureUnitService = measureUnitService;
        this.organizationService = organizationService;
        this.objectAccessService = objectAccessService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPoints(@PathVariable("contObjectId") Long contObjectId) {
		List<ContZPoint> zpList = contZPointService.findContObjectZPoints(contObjectId, portalUserIdsService.getCurrentIds());
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(zpList));
	}

//	/**
//	 *
//	 * @param contObjectId
//	 * @return
//	 */
//	@RequestMapping(value = "/contObjects/{contObjectId}/contZPointsEx", method = RequestMethod.GET,
//			produces = ApiConst.APPLICATION_JSON_UTF8)
//	public ResponseEntity<?> getContZPointsEx(@PathVariable("contObjectId") Long contObjectId) {
//		List<ContZPointEx> zpList = contZPointService.findContObjectZPointsEx(contObjectId);
//		return ApiResponse.responseOK(ObjectFilters.deletedFilter(zpList));
//	}

	/**
	 *
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPoints/vo", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointsVo(@PathVariable("contObjectId") Long contObjectId) {
		List<ContZPointFullVM> vmList = contZPointService.selectContObjectZPointsStatsVM(contObjectId,
            portalUserIdsService.getCurrentIds());
		return ApiResponse.responseOK(vmList);
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
		return ApiResponse.responseOK(resultList);
	}

    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @param contZPointFullVM
     * @return
     */
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContZPoint(@PathVariable("contObjectId") Long contObjectId,
			@PathVariable("contZPointId") Long contZPointId, @RequestBody ContZPointFullVM contZPointFullVM) {

		return ApiActionTool.processResponceApiActionUpdate(() -> {
            ContZPointFullVM result = contZPointService.updateDTO_safe(contZPointFullVM);
            result = contZPointService.saveContZPointTags(result, contZPointFullVM.getTagNames(), portalUserIdsService.getCurrentIds());
            return result;
        });

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

		ContZPointFullVM currentContZPoint = contZPointService.readContZPointTags(
		    contZPointService.findFullVM(contZPointId),
            portalUserIdsService.getCurrentIds());

		if (currentContZPoint == null || !currentContZPoint.getContObjectId().equals(contObjectId)) {
			return ApiResponse.responseBadRequest();
		}

		return ApiResponse.responseOK(currentContZPoint);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/zpoints", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPoints() {

		List<ContZPointDTO> contZPoints = objectAccessService.findAllContZPoints(portalUserIdsService.getCurrentIds().getSubscriberId());

		return ApiResponse.responseOK(contZPoints);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/zpoints/shortInfo", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContZPointsInfo() {

		List<ContZPointShortInfo> contZPoints = objectAccessService.findContZPointShortInfo(portalUserIdsService.getCurrentIds().getSubscriberId());

		return ApiResponse.responseOK(contZPoints);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/contServiceTypes", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContServiceTypes() {
		List<ContServiceType> contServiceTypes = contZPointService.selectContServiceTypes();

		return ApiResponse.responseOK(contServiceTypes);
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
			return ApiResponse.responseOK();
		}

		HashMap<Long, List<TimeDetailLastDate>> resultHWater = contServiceDataHWaterService
				.selectTimeDetailLastDateMapByPair(idPairList);

		HashMap<Long, List<TimeDetailLastDate>> resultEl = contServiceDataElService
				.selectTimeDetailLastDateMapByPair(idPairList);

		return ApiResponse.responseOK(ImmutableMap.builder().putAll(resultHWater).putAll(resultEl).build());
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

		List<Pair<String, Long>> serviceTypeContZPointIdList = contZPointService.selectContZPointServiceTypeIds(contObjectId);

		List<Pair<String, Long>> idPairList = serviceTypeContZPointIdList.stream().filter(i -> i.getRight().equals(contZPointId))
				.collect(Collectors.toList());

		HashMap<Long, List<TimeDetailLastDate>> resultHWater = contServiceDataHWaterService
				.selectTimeDetailLastDateMapByPair(idPairList);

		HashMap<Long, List<TimeDetailLastDate>> resultEl = contServiceDataElService
				.selectTimeDetailLastDateMapByPair(idPairList);

		return ApiResponse.responseOK(ImmutableMap.builder().putAll(resultHWater).putAll(resultEl).build());

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contObjects/rsoOrganizations", method = RequestMethod.GET)
	public ResponseEntity<?> getRsoOrganizations(
			@RequestParam(value = "organizationId", required = false) Long organizationId) {
		List<Organization> rsOrganizations = organizationService.selectRsoOrganizations(portalUserIdsService.getCurrentIds());

		List<Organization> resultList = SecurityUtils.isSystemUser() ? rsOrganizations
				: ObjectFilters.devModeFilter(rsOrganizations);

		organizationService.checkAndEnhanceOrganizations(resultList, organizationId);

		return ApiResponse.responseOK(resultList);
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


		if (!objectAccessService.checkContObjectId(contObjectId, portalUserIdsService.getCurrentIds())) {
			ApiResponse.responseForbidden();
		}

		List<ContZPointMetadata> result = contZPointMetadataService.selectContZPointMetadata(contZPointId);

		if (result == null || result.isEmpty()) {
			result = contZPointMetadataService.selectNewMetadata(contZPointId, true);
		}

		return ApiResponse.responseOK(result);

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

		return ApiResponse.responseOK(resultList);
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

        if (!objectAccessService.checkContObjectId(contObjectId, portalUserIdsService.getCurrentIds())) {
            ApiResponse.responseForbidden();
        }

		List<ContZPointMetadata> metadataList = contZPointMetadataService.selectNewMetadata(contZPointId, false);

		List<DeviceMetadataInfo> result = contZPointMetadataService.buildSrcPropsDeviceMapping(metadataList);

		return ApiResponse.responseOK(result);
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

        if (!objectAccessService.checkContObjectId(contObjectId, portalUserIdsService.getCurrentIds())) {
            ApiResponse.responseForbidden();
        }

		List<ContZPointMetadata> metadataList = contZPointMetadataService.selectNewMetadata(contZPointId, false);

		List<EntityColumn> result = contZPointMetadataService.buildDestProps(metadataList);

		return ApiResponse.responseOK(result);
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

        if (!objectAccessService.checkContObjectId(contObjectId, portalUserIdsService.getCurrentIds())) {
            ApiResponse.responseForbidden();
        }

		List<EntityColumn> result = contZPointMetadataService.selectContZPointDestDB(contZPointId);

		return ApiResponse.responseOK(result);
	}

}
