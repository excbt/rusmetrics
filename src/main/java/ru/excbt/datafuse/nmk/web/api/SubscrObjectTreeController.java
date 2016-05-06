package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
import ru.excbt.datafuse.nmk.data.model.types.ObjectTreeTypeKeyname;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrObjectTreeController extends SubscrApiController {

	@Autowired
	protected SubscrObjectTreeService subscrObjectTreeService;

	@Autowired
	protected SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

	@Autowired
	protected SubscrContObjectService subscrContObjectService;

	/**
	 * Same as RmaSubscrObjectTreeController
	 * 
	 * @param objectTreeType
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
	 * Same as RmaSubscrObjectTreeController
	 * 
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

		List<SubscrObjectTree> result = subscrObjectTreeService.selectSubscrObjectTreeShort(getRmaSubscriberId());
		return responseOK(ObjectFilters.deletedFilter(result));
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
			method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrObjectTreeContObjects(@PathVariable("objectTreeType") String objectTreeType,
			@PathVariable("rootSubscrObjectTreeId") Long rootSubscrObjectTreeId,
			@PathVariable("childSubscrObjectTreeId") Long childSubscrObjectTreeId) {

		ObjectTreeTypeKeyname treeType = ObjectTreeTypeKeyname.findByUrl(objectTreeType);

		if (treeType != ObjectTreeTypeKeyname.CONT_OBJECT_TREE_TYPE_1) {
			return responseBadRequest();
		}

		////
		List<ContObjectShortInfo> viewContObjectShortInfo = null;
		if (currentSubscriberService.isRma()) {
			//-- New version
			List<ContObjectShortInfo> rmaContObjectsShortInfo = subscrContObjectService
					.selectSubscriberContObjectsShortInfo(getSubscriberId());

			List<Long> rmaSubscrContObjectIds = subscrContObjectService.selectRmaSubscrContObjectIds(getSubscriberId());

			viewContObjectShortInfo = rmaContObjectsShortInfo.stream()
					.filter(i -> rmaSubscrContObjectIds.contains(i.getContObjectId())).collect(Collectors.toList());

		} else {
			viewContObjectShortInfo = subscrContObjectService.selectSubscriberContObjectsShortInfo(getSubscriberId());
		}
		////

		List<Long> treeContObjectIds = subscrObjectTreeContObjectService.selectTreeContObjectIds(getRmaSubscriberId(),
				childSubscrObjectTreeId);

		List<Long> resultContObjectIds = viewContObjectShortInfo.stream()
				.filter(i -> treeContObjectIds.contains(i.getContObjectId())).map(i -> i.getContObjectId())
				.collect(Collectors.toList());

		List<ContObject> resultList = new ArrayList<>();
		if (!resultContObjectIds.isEmpty()) {
			resultList = subscrContObjectService.selectSubscriberContObjects(getSubscriberId(), resultContObjectIds);
		}

		return responseOK(ObjectFilters.deletedFilter(resultList));
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

		List<Long> treeContObjectIds = subscrObjectTreeContObjectService
				.selectRmaTreeContObjectIdAllLevels(getRmaSubscriberId(), rootSubscrObjectTreeId);
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

		return responseOK(ObjectFilters.deletedFilter(result));
	}

}
