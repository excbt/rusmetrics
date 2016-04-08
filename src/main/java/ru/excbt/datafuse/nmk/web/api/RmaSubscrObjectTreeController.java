package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaSubscrObjectTreeController extends SubscrApiController {

	@Autowired
	private SubscrObjectTreeService subscrObjectTreeService;

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
	 * @param subscrObjectTreeId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{subscrObjectTreeId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("subscrObjectTreeId") Long subscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		SubscrObjectTree result = subscrObjectTreeService.findSubscrObjectTree(subscrObjectTreeId);
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
	public ResponseEntity<?> createContObjectTreeNode(@PathVariable("objectTreeType") String objectTreeType,
			@RequestBody ObjectNameHolder requestBody, HttpServletRequest request) {

		checkNotNull(requestBody);

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		SubscrObjectTree subscrObjectTree = subscrObjectTreeService.newSubscrObjectTree(getRmaSubscriberId(),
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
	 * @param subscrObjectTreeId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{subscrObjectTreeId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putContObjectNodeTree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("subscrObjectTreeId") Long subscrObjectTreeId, @RequestBody SubscrObjectTree requestEntity) {

		checkNotNull(requestEntity);
		checkArgument(subscrObjectTreeId.equals(requestEntity.getId()));

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
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
	 * @param subscrObjectTreeId
	 * @return
	 */
	@RequestMapping(value = "/subscrObjectTree/{objectTreeType}/{subscrObjectTreeId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContObjectNodeTree(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("subscrObjectTreeId") Long subscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				subscrObjectTreeService.deleteRootSubscrObjectTree(subscrObjectTreeId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);

	}

}
