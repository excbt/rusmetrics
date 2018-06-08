package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMonitorDTO;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatus;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatusV2;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeValidationService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User SubscrObjectTreeResource. It will be deleted
 */
@Deprecated
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrObjectTreeController {

	private static final String INVALID_SUBSCRIBER_MSG = "Invalid subscriberId (%d)";


    private final SubscrObjectTreeService subscrObjectTreeService;

    private final SubscrObjectTreeValidationService subscrObjectTreeValidationService;

    private final SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

    private final SubscrContEventNotificationService subscrContEventNotificationService;

    private final SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService;

    private final SubscrContEventNotificationStatusV2Service subscrContEventNotifiicationStatusV2Service;

	private final ContObjectService contObjectService;

	private final ObjectAccessService objectAccessService;

    private final PortalUserIdsService portalUserIdsService;

	@Autowired
    public SubscrObjectTreeController(SubscrObjectTreeService subscrObjectTreeService,
                                      SubscrObjectTreeValidationService subscrObjectTreeValidationService, SubscrObjectTreeContObjectService subscrObjectTreeContObjectService,
                                      SubscrContEventNotificationService subscrContEventNotificationService,
                                      SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService,
                                      SubscrContEventNotificationStatusV2Service subscrContEventNotifiicationStatusV2Service,
                                      ContObjectService contObjectService, ObjectAccessService objectAccessService, PortalUserIdsService portalUserIdsService) {
        this.subscrObjectTreeService = subscrObjectTreeService;
        this.subscrObjectTreeValidationService = subscrObjectTreeValidationService;
        this.subscrObjectTreeContObjectService = subscrObjectTreeContObjectService;
        this.subscrContEventNotificationService = subscrContEventNotificationService;
        this.subscrContEventNotifiicationStatusService = subscrContEventNotifiicationStatusService;
        this.subscrContEventNotifiicationStatusV2Service = subscrContEventNotifiicationStatusV2Service;
        this.contObjectService = contObjectService;
        this.objectAccessService = objectAccessService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 *
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since 08.04.2016
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ObjectNameVM {
		private String objectName;

		private Long templateId;

		public String getObjectName() {
			return objectName;
		}

		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

		public Long getTemplateId() {
			return templateId;
		}

		public void setTemplateId(Long templateId) {
			this.templateId = templateId;
		}

	}

	/**
	 * Same as RmaSubscrObjectTreeController
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		SubscrObjectTree result = subscrObjectTreeService.selectSubscrObjectTree(rootSubscrObjectTreeId);
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 * Same as RmaSubscrObjectTreeController
	 *
	 *
	 * @param objectTreeType
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeList(@PathVariable("objectTreeType") String objectTreeType) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		List<SubscrObjectTree> result = subscrObjectTreeService.selectSubscrObjectTreeShort(portalUserIdsService.getCurrentIds());
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @param childSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(
			value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}/node/{childSubscrObjectTreeId}/contObjects",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeContObjects(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}


        ApiAction action = new ApiActionEntityAdapter<Object>() {
            @Override
            public Object processAndReturnResult() {
                List<ContObject> resultList = subscrObjectTreeContObjectService.selectTreeContObjects(portalUserIdsService.getCurrentIds(),
                    childSubscrObjectTreeId);

                return contObjectService.wrapContObjectsMonitorDTO(portalUserIdsService.getCurrentIds(), resultList);
            }
        };

        return ApiActionTool.processResponceApiActionOk(action);



	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}/contObjects/free",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeContObjectsFree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		List<Long> contObjectIds = subscrObjectTreeContObjectService
				.selectTreeContObjectIdsAllLevels(portalUserIdsService.getCurrentIds(), rootSubscrObjectTreeId);
		checkNotNull(contObjectIds);

        ApiAction action = new ApiActionEntityAdapter<Object>() {
            @Override
            public Object processAndReturnResult() {
                List<ContObject> resultList = objectAccessService.findContObjectsExcludingIds(portalUserIdsService.getCurrentIds().getSubscriberId(), contObjectIds);
                return contObjectService.wrapContObjectsMonitorDTO(portalUserIdsService.getCurrentIds(), ObjectFilters.deletedFilter(resultList));
            }
        };

        return ApiActionTool.processResponceApiActionOk(action);

	}


	/**
	 *
	 * @param requestBody
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}", method = RequestMethod.POST,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
                                                    @RequestBody ObjectNameVM requestBody, HttpServletRequest request) {

		checkNotNull(requestBody);

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		SubscrObjectTree subscrObjectTree = subscrObjectTreeService.newSubscrObjectTree(portalUserIdsService.getCurrentIds(),
				requestBody.getTemplateId());

		if (requestBody.getObjectName() != null) {
			subscrObjectTree.setObjectName(requestBody.getObjectName());
		}

		ApiActionLocation action = new ApiActionEntityLocationAdapter<SubscrObjectTree, Long>(subscrObjectTree,
				request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscrObjectTree processAndReturnResult() {
				subscrObjectTreeService.initSubscrObjectTree(entity, ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1);
				subscrObjectTreeService.checkSubscrObjectTreeSubscriber(subscrObjectTree);
				return subscrObjectTreeService.saveRootSubscrObjectTree(entity);
			}

		};

		return ApiActionTool.processResponceApiActionCreate(action);
	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}", method = RequestMethod.PUT,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@RequestBody SubscrObjectTree requestEntity) {

		checkNotNull(requestEntity);
		checkArgument(rootSubscrObjectTreeId.equals(requestEntity.getId()));

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkSubscriberResponse(rootSubscrObjectTreeId);
		if (checkResponse != null) {
			return checkResponse;
		}

		ApiAction action = new ApiActionEntityAdapter<SubscrObjectTree>(requestEntity) {

			@Override
			public SubscrObjectTree processAndReturnResult() {
				subscrObjectTreeService.initSubscrObjectTree(entity, ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1);
				subscrObjectTreeService.checkSubscrObjectTreeSubscriber(entity);
				return subscrObjectTreeService.saveRootSubscrObjectTree(entity);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}",
			method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkSubscriberResponse(rootSubscrObjectTreeId);
		if (checkResponse != null) {
			return checkResponse;
		}

		ApiAction action = (ApiActionAdapter) () -> subscrObjectTreeService.deleteRootSubscrObjectTree(portalUserIdsService.getCurrentIds(), rootSubscrObjectTreeId);

		return ApiActionTool.processResponceApiActionDelete(action);

	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @param childSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(
			value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}/node/{childSubscrObjectTreeId}",
			method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteSubscrObjectTreeChildNode(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkSubscriberResponse(rootSubscrObjectTreeId);
		if (checkResponse != null) {
			return checkResponse;
		}

		ApiAction action = (ApiActionAdapter) () -> subscrObjectTreeService.deleteChildSubscrObjectTreeNode(portalUserIdsService.getCurrentIds(), rootSubscrObjectTreeId,
                childSubscrObjectTreeId);

		return ApiActionTool.processResponceApiActionDelete(action);

	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @param childSubscrObjectTreeId
	 * @param contObjectIds
	 * @return
	 */
	@RequestMapping(
			value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}/node/{childSubscrObjectTreeId}/contObjects/add",
			method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrObjectTreeContObjectsAdd(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId,
			@RequestBody final List<Long> contObjectIds) {

		checkNotNull(contObjectIds);

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkSubscriberResponse(rootSubscrObjectTreeId);
		if (checkResponse != null) {
			return checkResponse;
		}

		List<Long> existsingContObjectIds = subscrObjectTreeContObjectService
				.selectTreeContObjectIdsAllLevels(portalUserIdsService.getCurrentIds(), rootSubscrObjectTreeId);

		for (Long id : contObjectIds) {
			if (existsingContObjectIds.contains(id)) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("ContObjectid (id=%d) already linked", id));
			}
		}

		ApiAction action = new ApiActionEntityAdapter<List<ContObjectMonitorDTO>>() {

			@Override
			public List<ContObjectMonitorDTO> processAndReturnResult() {
				subscrObjectTreeContObjectService.addTreeContObjects(portalUserIdsService.getCurrentIds(), childSubscrObjectTreeId,
						contObjectIds);


				List<ContObject> resultList = subscrObjectTreeContObjectService.selectTreeContObjects(portalUserIdsService.getCurrentIds(),
                    childSubscrObjectTreeId);

				return contObjectService.wrapContObjectsMonitorDTO(portalUserIdsService.getCurrentIds(), resultList);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @param childSubscrObjectTreeId
	 * @param contObjectIds
	 * @return
	 */
	@RequestMapping(
			value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}/node/{childSubscrObjectTreeId}/contObjects/remove",
			method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrObjectTreeContObjectsRemove(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId,
			@RequestBody final List<Long> contObjectIds) {

		checkNotNull(contObjectIds);

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkSubscriberResponse(rootSubscrObjectTreeId);
		if (checkResponse != null) {
			return checkResponse;
		}

		List<Long> existingContObjectIds = subscrObjectTreeContObjectService
				.selectTreeContObjectIdsAllLevels(portalUserIdsService.getCurrentIds(), rootSubscrObjectTreeId);

		for (Long id : contObjectIds) {
			if (!existingContObjectIds.contains(id)) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("ContObjectid (id=%d) is not linked", id));
			}
		}

		ApiAction action = new ApiActionEntityAdapter<List<ContObjectMonitorDTO>>() {

			@Override
			public List<ContObjectMonitorDTO> processAndReturnResult() {
				subscrObjectTreeContObjectService.removeTreeContObjects(portalUserIdsService.getCurrentIds(), childSubscrObjectTreeId,
						contObjectIds);

                List<ContObject> resultList = subscrObjectTreeContObjectService.selectTreeContObjects(portalUserIdsService.getCurrentIds(),
                    childSubscrObjectTreeId);

                return contObjectService.wrapContObjectsMonitorDTO(portalUserIdsService.getCurrentIds(), resultList);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	private ResponseEntity<?> checkSubscriberResponse(final Long subscrObjectTreeId) {

		Long rmaSubscriberId = portalUserIdsService.getCurrentIds().getRmaId();
		if (!subscrObjectTreeValidationService.checkValidSubscriberOk(portalUserIdsService.getCurrentIds(), subscrObjectTreeId)) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest(INVALID_SUBSCRIBER_MSG, rmaSubscriberId));
		}
		return null;
	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @param childSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(
			value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}/node/{childSubscrObjectTreeId}/contObjects/cityStatusCollapse",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeMonitor(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId,
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr,
			@RequestParam(value = "noGreenColor", required = false) Boolean noGreenColor) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		List<ContObject> contObjects = subscrObjectTreeContObjectService.selectTreeContObjects(portalUserIdsService.getCurrentIds(),
				childSubscrObjectTreeId);

		List<CityMonitorContEventsStatus> result = subscrContEventNotifiicationStatusService
				.selectCityMonitoryContEventsStatus(portalUserIdsService.getCurrentIds(), contObjects,
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), noGreenColor);

		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @param childSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(
			value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}/node/{childSubscrObjectTreeId}/contObjects/cityStatusCollapseV2",
			method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeMonitorV2(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId,
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr,
			@RequestParam(value = "noGreenColor", required = false) Boolean noGreenColor) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		if (datePeriodParser.isOk() && datePeriodParser.getLocalDatePeriod().isInvalidEq()) {
			return ResponseEntity.badRequest().body(String
					.format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
		}

		List<ContObject> contObjects = subscrObjectTreeContObjectService.selectTreeContObjects(portalUserIdsService.getCurrentIds(),
				childSubscrObjectTreeId);

		List<CityMonitorContEventsStatusV2> result = subscrContEventNotifiicationStatusV2Service
				.selectCityMonitoryContEventsStatusV2(portalUserIdsService.getCurrentIds(), contObjects,
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), noGreenColor);

		return ApiResponse.responseOK(result);
	}

}
