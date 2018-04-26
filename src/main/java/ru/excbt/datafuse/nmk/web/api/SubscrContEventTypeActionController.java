package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeAction;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventTypeActionService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с настройками действий для уведомлений
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
@RestController
@RequestMapping("/api/subscr/contEventType")
public class SubscrContEventTypeActionController  {

	private final SubscrContEventTypeActionService subscrContEventTypeActionService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrContEventTypeActionController(SubscrContEventTypeActionService subscrContEventTypeActionService, PortalUserIdsService portalUserIdsService) {
        this.subscrContEventTypeActionService = subscrContEventTypeActionService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/actions/available", method = RequestMethod.GET)
	public ResponseEntity<?> getAvailableContEventTypes() {
		List<ContEventType> result = subscrContEventTypeActionService.selectAvailableContEventTypes();
		return ApiResponse.responseOK(result);
	}

    /**
     *
     * @param contEventTypeId
     * @param actionList
     * @param request
     * @return
     */
	@RequestMapping(value = "/{contEventTypeId}/actions", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscrContEventTypeActions(
			@PathVariable(value = "contEventTypeId") Long contEventTypeId,
			@RequestBody List<SubscrContEventTypeAction> actionList, HttpServletRequest request) {

		checkNotNull(contEventTypeId);
		checkNotNull(actionList);

		List<ContEventType> availableTypes = subscrContEventTypeActionService.selectAvailableContEventTypes();

		Optional<ContEventType> checkContEventType = availableTypes.stream()
				.filter(i -> i.getId().equals(contEventTypeId)).findFirst();

		if (!checkContEventType.isPresent()) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("contEventTypeId = %d is not found", contEventTypeId));
		}

		ContEventType contEventType = checkContEventType.get();

		ApiAction action = new ApiActionEntityAdapter<List<SubscrContEventTypeAction>>() {

			@Override
			public List<SubscrContEventTypeAction> processAndReturnResult() {
				return subscrContEventTypeActionService.updateSubscrContEventTypeActions(
				        new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()),
						contEventType, actionList);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param contEventTypeId
	 * @return
	 */
	@RequestMapping(value = "/{contEventTypeId}/actions", method = RequestMethod.GET)
	public ResponseEntity<?> getContEventTypeActions(@PathVariable(value = "contEventTypeId") Long contEventTypeId) {
		List<SubscrContEventTypeAction> result = subscrContEventTypeActionService
				.selectSubscrContEventTypeActions(portalUserIdsService.getCurrentIds().getSubscriberId(), contEventTypeId);
		return ApiResponse.responseOK(result);
	}

}
