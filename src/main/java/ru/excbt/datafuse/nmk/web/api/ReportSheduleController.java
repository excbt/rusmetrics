package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ReportShedule;
import ru.excbt.datafuse.nmk.data.service.ReportSheduleService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/reportShedule")
public class ReportSheduleController extends WebApiController {

	@Autowired
	private ReportSheduleService reportSheduleService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/active", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getReportTemplatesCommerce() {
		List<ReportShedule> result = reportSheduleService.selectReportShedule(
				DateTime.now(), currentSubscriberService.getSubscriberId());
		return ResponseEntity.ok(result);
	}

}
