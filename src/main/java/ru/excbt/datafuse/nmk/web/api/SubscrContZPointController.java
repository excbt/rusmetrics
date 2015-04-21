package ru.excbt.datafuse.nmk.web.api;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointEx;
import ru.excbt.datafuse.nmk.data.service.ContZPointService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;


@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContZPointController {

	@Autowired
	private SubscriberService subscrUserService;
	
	@Autowired
	private ContZPointService contZPointService;
	
	
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints", method = RequestMethod.GET, produces = WebApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getZPoints(@PathVariable("contObjectId") long contObjectId) {
		List<ContZPoint> zpList = contZPointService.findContZPoints(contObjectId);
		return ResponseEntity.ok(zpList);
	}
	
	@RequestMapping(value = "/contObjects/{contObjectId}/contZPointsEx", method = RequestMethod.GET, produces = WebApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getZPointsEx(@PathVariable("contObjectId") long contObjectId) {
		List<ContZPointEx> zpList = contZPointService.findContZPointsEx(contObjectId);
		return ResponseEntity.ok(zpList);
	}
	
}
