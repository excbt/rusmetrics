package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSms;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSmsAddr;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrContEventTypeSmsService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionEntityLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с настройками смс уведомлений
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.12.2015
 *
 */
@RestController
@RequestMapping("/api/subscr/contEventSms")
public class SubscrContEventTypeSmsController  {

	private final SubscrContEventTypeSmsService subscrContEventTypeSmsService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrContEventTypeSmsController(SubscrContEventTypeSmsService subscrContEventTypeSmsService, PortalUserIdsService portalUserIdsService) {
        this.subscrContEventTypeSmsService = subscrContEventTypeSmsService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
	 *
	 * @return
	 */
	@RequestMapping(value = "/availableContEventTypes", method = RequestMethod.GET)
	public ResponseEntity<?> getAvailableSmsContEventTypes() {
		List<ContEventType> result = subscrContEventTypeSmsService.selectAvailableContEventTypes();
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/contEventTypes", method = RequestMethod.GET)
	public ResponseEntity<?> getSmsContEventTypes() {
		List<SubscrContEventTypeSms> result = subscrContEventTypeSmsService
				.selectSubscrContEventTypeSms(portalUserIdsService.getCurrentIds().getSubscriberId());
		return ApiResponse.responseOK(result);
	}

	/**
	 *
	 * @param contEventTypeId
	 * @param smsAddrList
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/contEventTypes", method = RequestMethod.POST, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createSubscrContEventTypeSms(@RequestParam(value = "contEventTypeId") Long contEventTypeId,
			@RequestBody List<SubscrContEventTypeSmsAddr> smsAddrList, HttpServletRequest request) {

		checkNotNull(contEventTypeId);
		checkNotNull(smsAddrList);

		List<ContEventType> availableTypes = subscrContEventTypeSmsService.selectAvailableContEventTypes();

		Optional<ContEventType> checkContEventType = availableTypes.stream()
				.filter(i -> i.getId().equals(contEventTypeId)).findFirst();

		if (!checkContEventType.isPresent()) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("contEventTypeId = %d is not found", contEventTypeId));
		}

		ContEventType contEventType = checkContEventType.get();

		ApiActionLocation action = new ApiActionEntityLocationAdapter<SubscrContEventTypeSms, Long>(request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscrContEventTypeSms processAndReturnResult() {
			    Subscriber subscriber = new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId());
				return subscrContEventTypeSmsService.createSubscrContEventTypeSms(subscriber, contEventType,
						smsAddrList);
			}
		};

		return ApiActionTool.processResponceApiActionCreate(action);
	}

}
