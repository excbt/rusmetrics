package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectNodeTree;
import ru.excbt.datafuse.nmk.data.service.ContObjectNodeTreeService;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaContObjectNodeTreeController extends SubscrApiController {

	@Autowired
	private ContObjectNodeTreeService contObjectNodeTreeService;

	@Autowired
	private ContObjectService contObjectService;

	/**
	 * 
	 * @param contObjectNodeTreeId
	 * @return
	 */
	@RequestMapping(value = "/contObjectNodeTree/{contObjectNodeTreeId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectNodeTree(@PathVariable("contObjectNodeTreeId") Long contObjectNodeTreeId) {
		ContObjectNodeTree result = contObjectNodeTreeService.findContObjectNodeTree(contObjectNodeTreeId);
		return responseOK(ObjectFilters.deletedFilter(result));
	}

	/**
	 * 
	 * @param contObjectNodeTreeId
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/contObjectNodeTree/{contObjectNodeTreeId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> putContObjectNodeTree(@PathVariable("contObjectNodeTreeId") Long contObjectNodeTreeId,
			@RequestBody ContObjectNodeTree requestEntity) {

		checkNotNull(requestEntity);
		checkArgument(contObjectNodeTreeId.equals(requestEntity.getId()));

		ApiAction action = new ApiActionEntityAdapter<ContObjectNodeTree>(requestEntity) {

			@Override
			public ContObjectNodeTree processAndReturnResult() {
				contObjectNodeTreeService.initRootNodeTree(requestEntity, ContObjectNodeTreeService.NODE_TREE_TYPE_1);
				return contObjectNodeTreeService.saveRootNode(requestEntity);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@RequestMapping(value = "/contObjectNodeTree/byContObject/{contObjectId}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectNodeTreeByContObject(@PathVariable("contObjectId") Long contObjectId) {
		List<ContObjectNodeTree> resultList = contObjectNodeTreeService
				.selectContObjectNodeTreeByContObject(contObjectId);

		if (resultList.isEmpty()) {
			ContObject contObject = contObjectService.findContObject(contObjectId);
			ContObjectNodeTree newNode = contObjectNodeTreeService.newRootContObjectNode(contObject);
			resultList.add(newNode);
		}

		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

}
