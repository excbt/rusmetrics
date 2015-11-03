package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ContGroup;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.ContGroupService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiAction;
import ru.excbt.datafuse.nmk.web.api.support.AbstractEntityApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/contGroup")
public class ContGroupController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetController.class);

	@Autowired
	private ContGroupService contGroupService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{contGroupId}/contObject", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectItems(@PathVariable(value = "contGroupId") Long contGroupId) {

		checkNotNull(contGroupId);
		List<ContObject> resultList = contGroupService.selectContGroupObjects(contGroupId, getCurrentSubscriberId());

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{contGroupId}/contObject/available", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAvailableContObjectItems(@PathVariable(value = "contGroupId") Long contGroupId) {

		checkNotNull(contGroupId);
		List<ContObject> resultList = contGroupService.selectAvailableContGroupObjects(contGroupId,
				currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscriberGroups() {

		List<ContGroup> resultList = contGroupService
				.selectSubscriberGroups(currentSubscriberService.getSubscriberId());

		return ResponseEntity.ok(resultList);
	}

	@RequestMapping(value = "", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOne(
			@RequestParam(value = "contObjectIds", required = false) final Long[] contObjectIds,
			@RequestBody ContGroup contGroup, HttpServletRequest request) {

		checkArgument(contGroup.isNew());
		contGroup.setSubscriber(currentSubscriberService.getSubscriber());

		ApiActionLocation action = new AbstractEntityApiActionLocation<ContGroup, Long>(contGroup, request) {

			@Override
			public void process() {
				ContGroup newObject = contGroupService.createOne(entity, contObjectIds);
				setResultEntity(newObject);
			}

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param contGroupId
	 * @return
	 */
	@RequestMapping(value = "{contGroupId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOne(@PathVariable(value = "contGroupId") final Long contGroupId) {

		ApiAction action = new AbstractApiAction() {
			@Override
			public void process() {
				contGroupService.deleteOne(contGroupId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @param contGroupId
	 * @return
	 */
	@RequestMapping(value = "{contGroupId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOne(@PathVariable(value = "contGroupId") Long contGroupId,
			@RequestParam(value = "contObjectIds", required = false) final Long[] contObjectIds,
			@RequestBody ContGroup contGroup) {

		checkNotNull(contGroupId);
		checkNotNull(contGroup);
		checkNotNull(contGroup.getId());
		checkArgument(contGroup.getId().equals(contGroupId));

		contGroup.setSubscriber(currentSubscriberService.getSubscriber());

		ApiAction action = new AbstractEntityApiAction<ContGroup>(contGroup) {
			@Override
			public void process() {
				setResultEntity(contGroupService.updateOne(entity, contObjectIds));
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

}
