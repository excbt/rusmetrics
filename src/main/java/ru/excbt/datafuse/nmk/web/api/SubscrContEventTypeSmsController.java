package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSms;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSmsAddr;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventTypeSmsService;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Контроллер для работы с настройками смс уведомлений
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
@Controller
@RequestMapping("/api/subscr/contEventSms")
public class SubscrContEventTypeSmsController extends SubscrApiController {

	@Autowired
	private SubscrContEventTypeSmsService subscrContEventTypeSmsService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/availableContEventTypes", method = RequestMethod.GET)
	public ResponseEntity<?> getAvailableSmsContEventTypes() {
		List<ContEventType> result = subscrContEventTypeSmsService.selectAvailableContEventTypes();
		return responseOK(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contEventTypes", method = RequestMethod.GET)
	public ResponseEntity<?> getSmsContEventTypes() {
		List<SubscrContEventTypeSms> result = subscrContEventTypeSmsService
				.selectSubscrContEventTypeSms(getCurrentSubscriberId());
		return responseOK(result);
	}

	/**
	 * 
	 * @param contEventTypeId
	 * @param smsAddrList
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/contEventTypes", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createSubscrContEventTypeSms(@RequestParam(value = "contEventTypeId") Long contEventTypeId,
			@RequestBody List<SubscrContEventTypeSmsAddr> smsAddrList, HttpServletRequest request) {

		checkNotNull(contEventTypeId);
		checkNotNull(smsAddrList);

		List<ContEventType> availableTypes = subscrContEventTypeSmsService.selectAvailableContEventTypes();

		Optional<ContEventType> checkContEventType = availableTypes.stream()
				.filter(i -> i.getId().equals(contEventTypeId)).findFirst();

		if (!checkContEventType.isPresent()) {
			return responseBadRequest(ApiResult.validationError("contEventTypeId = %d is not found", contEventTypeId));
		}

		ContEventType contEventType = checkContEventType.get();

		ApiActionLocation action = new ApiActionEntityLocationAdapter<SubscrContEventTypeSms, Long>(request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscrContEventTypeSms processAndReturnResult() {
				return subscrContEventTypeSmsService.createSubscrContEventTypeSms(getCurrentSubscriber(), contEventType,
						smsAddrList);
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

}
