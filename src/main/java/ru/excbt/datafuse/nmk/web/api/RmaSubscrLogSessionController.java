package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.LogSessionStep;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.vo.LogSessionVO;
import ru.excbt.datafuse.nmk.data.service.LogSessionService;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceService;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping(value = "/api/rma/logSessions")
public class RmaSubscrLogSessionController extends SubscrApiController {

	@Autowired
	private LogSessionService logSessionService;

	@Autowired
	private SubscrDataSourceService subscrDataSourceService;

	/**
	 * 
	 * @param fromDateStr
	 * @param toDateStr
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getLogSessionVO(
			@RequestParam(value = "fromDate", required = true) String fromDateStr,
			@RequestParam(value = "toDate", required = true) String toDateStr,
			@RequestParam(value = "contObjectIds", required = false) List<Long> contObjectIds) {

		LocalDatePeriodParser datePeriodParser = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

		checkNotNull(datePeriodParser);

		ResponseEntity<?> checkPeriod = checkDatePeriodArguments(datePeriodParser);
		if (checkPeriod != null) {
			return checkPeriod;
		}

		List<Long> dataSourceIds = subscrDataSourceService.selectDataSourceIdsBySubscriber(getSubscriberId());

		if (contObjectIds != null && contObjectIds.size() > 0) {
			if (!canAccessContObject(contObjectIds.toArray(new Long[] {}))) {
				return responseForbidden();
			}
		}

		List<LogSessionVO> resultList = new ArrayList<>();

		if (contObjectIds != null && contObjectIds.size() > 0) {
			resultList = logSessionService.selectLogSessions(dataSourceIds,
					datePeriodParser.getLocalDatePeriod().buildEndOfDay(), contObjectIds);

		} else {

			resultList = logSessionService.selectLogSessions(dataSourceIds,
					datePeriodParser.getLocalDatePeriod().buildEndOfDay());

		}

		return responseOK(resultList);
	}

	/**
	 * 
	 * @param logSessionId
	 * @return
	 */
	@RequestMapping(value = "/{logSessionId}/steps", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getLogSessionStep(@PathVariable("logSessionId") Long logSessionId) {
		List<LogSessionStep> resultList = logSessionService.selectLogSessionSteps(logSessionId);
		return responseOK(ObjectFilters.deletedFilter(resultList));
	}

}
