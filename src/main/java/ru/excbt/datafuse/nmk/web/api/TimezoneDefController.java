package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.data.service.TimezoneDefService;

@Controller
@RequestMapping(value = "/api/timezoneDef")
public class TimezoneDefController extends WebApiController {

	@Autowired
	private TimezoneDefService timezoneDefService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<?> getTimezoneDef() {
		List<TimezoneDef> resultList = timezoneDefService.findAll();
		return responseOK(resultList);
	}
}