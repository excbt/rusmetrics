package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.SubscrContGroup;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.ContGroupService;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с группой объектов учета
 *
 * @author STA.Kuzovoy
 * @version 1.0
 * @since 29.05.2015
 *
 */
@Controller
@RequestMapping(value = "/api/subscr/contGroup")
public class SubscrContGroupController {

	private static final Logger logger = LoggerFactory.getLogger(ReportParamsetController.class);

	private final ContGroupService contGroupService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrContGroupController(ContGroupService contGroupService, PortalUserIdsService portalUserIdsService) {
        this.contGroupService = contGroupService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/{contGroupId}/contObject", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getGroupContObjects(@PathVariable(value = "contGroupId") Long contGroupId) {

		checkNotNull(contGroupId);

		ApiActionObjectProcess action = () -> contGroupService.selectContGroupObjects(portalUserIdsService.getCurrentIds(), contGroupId);

		//List<ContObject> resultList = contGroupService.selectContGroupObjects(getSubscriberParam(), contGroupId);

		return ApiResponse.responseOK(action);// ResponseEntity.ok(resultList);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/{contGroupId}/contObject/available", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getAvailableContObjectItems(@PathVariable(value = "contGroupId") Long contGroupId) {

		checkNotNull(contGroupId);

		ApiActionObjectProcess action = () -> {
			return contGroupService.selectAvailableContGroupObjects(portalUserIdsService.getCurrentIds(), contGroupId);
		};

		return ApiResponse.responseOK(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscriberGroups() {

		ApiActionObjectProcess action = () -> {
			return contGroupService.selectSubscriberGroups(portalUserIdsService.getCurrentIds());
		};

		return ApiResponse.responseOK(action);

	}

	/**
	 *
	 * @param contObjectIds
	 * @param contGroup
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createGroup(
			@RequestParam(value = "contObjectIds", required = false) final Long[] contObjectIds,
			@RequestBody final SubscrContGroup contGroup, final HttpServletRequest request) {

		checkArgument(contGroup.isNew());

		ApiActionProcess<SubscrContGroup> process = () -> {
			contGroup.setSubscriber(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
			return contGroupService.createOne(contGroup, contObjectIds);
		};

		return ApiResponse.responseCreate(process, () -> request.getRequestURI());

	}

	/**
	 *
	 * @param contGroupId
	 * @return
	 */
	@RequestMapping(value = "{contGroupId}", method = RequestMethod.DELETE, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteGroup(@PathVariable(value = "contGroupId") final Long contGroupId) {

		ApiActionVoidProcess action = () -> {
			contGroupService.deleteOne(contGroupId);
		};

		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @param contGroupId
	 * @return
	 */
	@RequestMapping(value = "{contGroupId}", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateGroup(@PathVariable(value = "contGroupId") final Long contGroupId,
			@RequestParam(value = "contObjectIds", required = false) final Long[] contObjectIds,
			@RequestBody final SubscrContGroup contGroup) {

		checkNotNull(contGroupId);
		checkNotNull(contGroup);
		checkNotNull(contGroup.getId());
		checkArgument(contGroup.getId().equals(contGroupId));

		ApiActionObjectProcess action = () -> {
			contGroup.setSubscriber(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()));
			return contGroupService.updateOne(contGroup, contObjectIds);
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

}
