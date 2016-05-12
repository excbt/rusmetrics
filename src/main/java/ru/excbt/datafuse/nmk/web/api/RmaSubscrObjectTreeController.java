package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaSubscrObjectTreeController extends SubscrApiController {

	private static final String INVALID_RMA_SUBSCRIBER_MSG = "Invalid rmaSubscriberId (%d)";

	@Autowired
	private SubscrObjectTreeService subscrObjectTreeService;

	@Autowired
	private SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

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
	 * 
	 * @param rootSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		SubscrObjectTree result = subscrObjectTreeService.findSubscrObjectTree(rootSubscrObjectTreeId);
		return responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 * 
	 * @param objectTreeType
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeList(@PathVariable("objectTreeType") String objectTreeType) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		List<SubscrObjectTree> result = subscrObjectTreeService.selectSubscrObjectTreeShort(getSubscriberParam());
		return responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}", method = RequestMethod.POST,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
			@RequestBody ObjectNameHolder requestBody, HttpServletRequest request) {

		checkNotNull(requestBody);

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		SubscrObjectTree subscrObjectTree = subscrObjectTreeService.newSubscrObjectTree(getSubscriberParam(),
				requestBody.templateId);

		if (requestBody.objectName != null) {
			subscrObjectTree.setObjectName(requestBody.objectName);
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

				return subscrObjectTreeService.saveRootSubscrObjectTree(entity);
			}

		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@RequestBody SubscrObjectTree requestEntity) {

		checkNotNull(requestEntity);
		checkArgument(rootSubscrObjectTreeId.equals(requestEntity.getId()));

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkRmaSubscriberResponse(rootSubscrObjectTreeId);
		if (checkResponse != null) {
			return checkResponse;
		}

		ApiAction action = new ApiActionEntityAdapter<SubscrObjectTree>(requestEntity) {

			@Override
			public SubscrObjectTree processAndReturnResult() {
				subscrObjectTreeService.initSubscrObjectTree(entity, ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1);
				return subscrObjectTreeService.saveRootSubscrObjectTree(entity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}",
			method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkRmaSubscriberResponse(rootSubscrObjectTreeId);
		if (checkResponse != null) {
			return checkResponse;
		}

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				subscrObjectTreeService.deleteRootSubscrObjectTree(getSubscriberParam(), rootSubscrObjectTreeId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);

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
			method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteSubscrObjectTreeChildNode(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkRmaSubscriberResponse(rootSubscrObjectTreeId);
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

		return WebApiHelper.processResponceApiActionDelete(action);

	}

	/**
	 * 
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(
			value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}/node/{childSubscrObjectTreeId}/contObjects",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeContObjects(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		List<ContObject> result = subscrObjectTreeContObjectService.selectRmaTreeContObjects(getSubscriberParam(),
				childSubscrObjectTreeId);

		return responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 * 
	 * @param objectTreeType
	 * @param rootSubscrObjectTreeId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{rootSubscrObjectTreeId}/contObjects/free",
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeContObjectsFree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		List<Long> contObjectIds = subscrObjectTreeContObjectService
				.selectTreeContObjectIdsAllLevels(getSubscriberParam(), rootSubscrObjectTreeId);
		checkNotNull(contObjectIds);

		List<ContObject> result = subscrContObjectService
				.selectRmaSubscriberContObjectsExcludingIds(getRmaSubscriberId(), contObjectIds);

		return responseOK(ObjectFilters.deletedFilter(result));
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
			method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrObjectTreeContObjectsAdd(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId,
			@RequestBody final List<Long> contObjectIds) {

		checkNotNull(contObjectIds);

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkRmaSubscriberResponse(rootSubscrObjectTreeId);
		if (checkResponse != null) {
			return checkResponse;
		}

		List<Long> existsingContObjectIds = subscrObjectTreeContObjectService
				.selectTreeContObjectIdsAllLevels(getSubscriberParam(), rootSubscrObjectTreeId);

		for (Long id : contObjectIds) {
			if (existsingContObjectIds.contains(id)) {
				return responseBadRequest(ApiResult.validationError("ContObjectid (id=%d) already linked", id));
			}
		}

		ApiAction action = new ApiActionEntityAdapter<List<ContObject>>() {

			@Override
			public List<ContObject> processAndReturnResult() {
				subscrObjectTreeContObjectService.addTreeContObjects(getSubscriberParam(), childSubscrObjectTreeId,
						contObjectIds);
				return subscrObjectTreeContObjectService.selectRmaTreeContObjects(getSubscriberParam(),
						childSubscrObjectTreeId);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
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
			method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putSubscrObjectTreeContObjectsRemove(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId,
			@RequestBody final List<Long> contObjectIds) {

		checkNotNull(contObjectIds);

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		ResponseEntity<?> checkResponse = checkRmaSubscriberResponse(rootSubscrObjectTreeId);
		if (checkResponse != null) {
			return checkResponse;
		}

		Long rmaSubscriberId = getRmaSubscriberId();
		if (!rmaSubscriberId.equals(subscrObjectTreeService.selectRmaSubscriberId(rootSubscrObjectTreeId))) {
			return responseBadRequest(ApiResult.badRequest(INVALID_RMA_SUBSCRIBER_MSG, rmaSubscriberId));
		}

		List<Long> existsingContObjectIds = subscrObjectTreeContObjectService
				.selectTreeContObjectIdsAllLevels(getSubscriberParam(), rootSubscrObjectTreeId);

		for (Long id : contObjectIds) {
			if (!existsingContObjectIds.contains(id)) {
				return responseBadRequest(ApiResult.validationError("ContObjectid (id=%d) is not linked", id));
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

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param subscrObjectTreeId
	 * @return
	 */
	private ResponseEntity<?> checkRmaSubscriberResponse(final Long subscrObjectTreeId) {
		Long rmaSubscriberId = getRmaSubscriberId();
		if (rmaSubscriberId == null
				|| !rmaSubscriberId.equals(subscrObjectTreeService.selectRmaSubscriberId(subscrObjectTreeId))) {
			return responseBadRequest(ApiResult.badRequest(INVALID_RMA_SUBSCRIBER_MSG, rmaSubscriberId));
		}
		return null;
	}

}
