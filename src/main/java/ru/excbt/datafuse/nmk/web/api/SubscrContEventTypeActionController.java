package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeAction;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventTypeActionService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityAdapter;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с настройками действий для уведомлений
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
@Controller
@RequestMapping("/api/subscr/contEventType")
public class SubscrContEventTypeActionController extends SubscrApiController {

	@Autowired
	private SubscrContEventTypeActionService subscrContEventTypeActionService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/actions/available", method = RequestMethod.GET)
	public ResponseEntity<?> getAvailableContEventTypes() {
		List<ContEventType> result = subscrContEventTypeActionService.selectAvailableContEventTypes();
		return responseOK(result);
	}

	/**
	 * 
	 * @param contEventTypeId
	 * @param smsAddrList
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{contEventTypeId}/actions", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateSubscrContEventTypeActions(
			@PathVariable(value = "contEventTypeId") Long contEventTypeId,
			@RequestBody List<SubscrContEventTypeAction> actionList, HttpServletRequest request) {

		checkNotNull(contEventTypeId);
		checkNotNull(actionList);

		List<ContEventType> availableTypes = subscrContEventTypeActionService.selectAvailableContEventTypes();

		Optional<ContEventType> checkContEventType = availableTypes.stream()
				.filter(i -> i.getId().equals(contEventTypeId)).findFirst();

		if (!checkContEventType.isPresent()) {
			return responseBadRequest(ApiResult.validationError("contEventTypeId = %d is not found", contEventTypeId));
		}

		ContEventType contEventType = checkContEventType.get();

		ApiAction action = new ApiActionEntityAdapter<List<SubscrContEventTypeAction>>() {

			@Override
			public List<SubscrContEventTypeAction> processAndReturnResult() {
				return subscrContEventTypeActionService.updateSubscrContEventTypeActions(getCurrentSubscriber(),
						contEventType, actionList);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param contEventTypeId
	 * @return
	 */
	@RequestMapping(value = "/{contEventTypeId}/actions", method = RequestMethod.GET)
	public ResponseEntity<?> getContEventTypeActions(@PathVariable(value = "contEventTypeId") Long contEventTypeId) {
		List<SubscrContEventTypeAction> result = subscrContEventTypeActionService
				.selectSubscrContEventTypeActions(getCurrentSubscriberId(), contEventTypeId);
		return responseOK(result);
	}

}
