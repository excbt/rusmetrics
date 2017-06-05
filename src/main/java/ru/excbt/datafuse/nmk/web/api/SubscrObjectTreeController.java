package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatus;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatusV2;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrObjectTreeController extends AbstractSubscrApiResource {

	private static final String INVALID_SUBSCRIBER_MSG = "Invalid subscriberId (%d)";

	@Autowired
	protected SubscrObjectTreeService subscrObjectTreeService;

	@Autowired
	protected SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

	@Autowired
	protected SubscrContObjectService subscrContObjectService;

	@Autowired
	protected SubscrContEventNotificationService subscrContEventNotificationService;

	@Autowired
	protected SubscrContEventNotificationStatusService subscrContEventNotifiicationStatusService;

	@Autowired
	protected SubscrContEventNotificationStatusV2Service subscrContEventNotifiicationStatusV2Service;

	/**
	 *
	 *
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since 08.04.2016
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ObjectNameHolder {
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

		List<SubscrObjectTree> result = subscrObjectTreeService.selectSubscrObjectTreeShort(getSubscriberParam());
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

		List<ContObject> result = subscrObjectTreeContObjectService.selectTreeContObjects(getSubscriberParam(),
				childSubscrObjectTreeId);

		return ApiResponse.responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @param childSubscrObjectTreeId
	 * @return
	 */
	@Deprecated
	protected ResponseEntity<?> getSubscrObjectTreeContObjects_old(
			@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		////
		List<ContObjectShortInfo> viewContObjectShortInfo = null;
		if (currentSubscriberService.isRma()) {
			//-- New version
			List<ContObjectShortInfo> rmaContObjectsShortInfo = subscrContObjectService
					.selectSubscriberContObjectsShortInfo(getSubscriberId());

			List<Long> rmaSubscrContObjectIds = subscrContObjectService
					.selectRmaSubscribersContObjectIds(getSubscriberParam());

			viewContObjectShortInfo = rmaContObjectsShortInfo.stream()
					.filter(i -> rmaSubscrContObjectIds.contains(i.getContObjectId())).collect(Collectors.toList());

		} else {
			viewContObjectShortInfo = subscrContObjectService.selectSubscriberContObjectsShortInfo(getSubscriberId());
		}
		////

		List<Long> treeContObjectIds = subscrObjectTreeContObjectService.selectTreeContObjectIds(getSubscriberParam(),
				childSubscrObjectTreeId);

		List<Long> resultContObjectIds = viewContObjectShortInfo.stream()
				.filter(i -> treeContObjectIds.contains(i.getContObjectId())).map(i -> i.getContObjectId())
				.collect(Collectors.toList());

		List<ContObject> resultList = new ArrayList<>();
		if (!resultContObjectIds.isEmpty()) {
			resultList = subscrContObjectService.selectSubscriberContObjects(getSubscriberId(), resultContObjectIds);
		}

		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
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
				.selectTreeContObjectIdsAllLevels(getSubscriberParam(), rootSubscrObjectTreeId);
		checkNotNull(contObjectIds);

		//		List<ContObject> result = subscrContObjectService
		//				.selectRmaSubscriberContObjectsExcludingIds(getRmaSubscriberId(), contObjectIds);
		List<ContObject> result = subscrContObjectService.selectSubscriberContObjectsExcludingIds(getSubscriberId(),
				contObjectIds);

		return ApiResponse.responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 *
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @return
	 */
	@Deprecated
	protected ResponseEntity<?> getSubscrObjectTreeContObjectsFree_old(
			@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		List<Long> treeContObjectIds = subscrObjectTreeContObjectService
				.selectTreeContObjectIdsAllLevels(getSubscriberParam(), rootSubscrObjectTreeId);
		checkNotNull(treeContObjectIds);

		////
		List<ContObject> viewContObjects = null;
		if (currentSubscriberService.isRma()) {
			List<ContObject> rmaContObjects = subscrContObjectService
					.selectRmaSubscriberContObjectsExcludingIds(getSubscriberId(), treeContObjectIds);
			viewContObjects = rmaContObjects.stream().filter(i -> !Boolean.FALSE.equals(i.get_haveSubscr()))
					.collect(Collectors.toList());
		} else {
			viewContObjects = subscrContObjectService.selectSubscriberContObjectsExcludingIds(getSubscriberId(),
					treeContObjectIds);
		}
		////

		List<ContObject> result = viewContObjects;

		return ApiResponse.responseOK(ObjectFilters.deletedFilter(result));
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
			@RequestBody ObjectNameHolder requestBody, HttpServletRequest request) {

		checkNotNull(requestBody);

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return ApiResponse.responseBadRequest();
		}

		SubscrObjectTree subscrObjectTree = subscrObjectTreeService.newSubscrObjectTree(getSubscriberParam(),
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

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				subscrObjectTreeService.deleteRootSubscrObjectTree(getSubscriberParam(), rootSubscrObjectTreeId);
			}
		};

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

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				subscrObjectTreeService.deleteChildSubscrObjectTreeNode(getSubscriberParam(), rootSubscrObjectTreeId,
						childSubscrObjectTreeId);
			}
		};

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
				.selectTreeContObjectIdsAllLevels(getSubscriberParam(), rootSubscrObjectTreeId);

		for (Long id : contObjectIds) {
			if (existsingContObjectIds.contains(id)) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("ContObjectid (id=%d) already linked", id));
			}
		}

		ApiAction action = new ApiActionEntityAdapter<List<ContObject>>() {

			@Override
			public List<ContObject> processAndReturnResult() {
				subscrObjectTreeContObjectService.addTreeContObjects(getSubscriberParam(), childSubscrObjectTreeId,
						contObjectIds);
				return subscrObjectTreeContObjectService.selectTreeContObjects(getSubscriberParam(),
						childSubscrObjectTreeId);
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

		List<Long> existsingContObjectIds = subscrObjectTreeContObjectService
				.selectTreeContObjectIdsAllLevels(getSubscriberParam(), rootSubscrObjectTreeId);

		for (Long id : contObjectIds) {
			if (!existsingContObjectIds.contains(id)) {
				return ApiResponse.responseBadRequest(ApiResult.validationError("ContObjectid (id=%d) is not linked", id));
			}
		}

		ApiAction action = new ApiActionEntityAdapter<List<ContObject>>() {

			@Override
			public List<ContObject> processAndReturnResult() {
				subscrObjectTreeContObjectService.deleteTreeContObjects(getSubscriberParam(), childSubscrObjectTreeId,
						contObjectIds);
				return subscrObjectTreeContObjectService.selectTreeContObjects(getSubscriberParam(),
						childSubscrObjectTreeId);
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

		Long rmaSubscriberId = getRmaSubscriberId();
		if (!subscrObjectTreeService.checkValidSubscriberOk(getSubscriberParam(), subscrObjectTreeId)) {
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

		List<ContObject> contObjects = subscrObjectTreeContObjectService.selectTreeContObjects(getSubscriberParam(),
				childSubscrObjectTreeId);

		List<CityMonitorContEventsStatus> result = subscrContEventNotifiicationStatusService
				.selectCityMonitoryContEventsStatus(getSubscriberParam(), contObjects,
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

		List<ContObject> contObjects = subscrObjectTreeContObjectService.selectTreeContObjects(getSubscriberParam(),
				childSubscrObjectTreeId);

		List<CityMonitorContEventsStatusV2> result = subscrContEventNotifiicationStatusV2Service
				.selectCityMonitoryContEventsStatusV2(getSubscriberParam(), contObjects,
						datePeriodParser.getLocalDatePeriod().buildEndOfDay(), noGreenColor);

		return ApiResponse.responseOK(result);
	}

}
