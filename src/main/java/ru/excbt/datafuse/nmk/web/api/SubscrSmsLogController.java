package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrSmsLog;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrSmsLogService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@RestController
@RequestMapping("/api/subscr/smsLog")
public class SubscrSmsLogController {

	private final SubscrSmsLogService subscrSmsLogService;

    private final PortalUserIdsService portalUserIdsService;

    public SubscrSmsLogController(SubscrSmsLogService subscrSmsLogService, PortalUserIdsService portalUserIdsService) {
        this.subscrSmsLogService = subscrSmsLogService;
        this.portalUserIdsService = portalUserIdsService;
    }


    /**
	 *
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET,
			produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrSmsLog(@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr) {

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		ResponseEntity<?> checkPeriod = ApiResponse.checkDatePeriodArguments(datePeriodParser);
		if (checkPeriod != null) {
			return checkPeriod;
		}

		List<SubscrSmsLog> resultList = subscrSmsLogService.selectSmsLog(portalUserIdsService.getCurrentIds().getRmaId(),
				datePeriodParser.getLocalDatePeriod().buildEndOfDay());
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(resultList));
	}
}
