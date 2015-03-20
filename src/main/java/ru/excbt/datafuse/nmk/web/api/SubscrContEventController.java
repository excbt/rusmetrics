package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContEventController {

	@Autowired
	private SubscrUserService subscrUserService;

	@RequestMapping(value = "/contObjects/{contObjectId}/events", method = RequestMethod.GET, produces = WebApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll(@PathVariable("contObjectId") long contObjectId) {
		List<ContEvent> result = subscrUserService.findContEvents(contObjectId);
		return ResponseEntity.ok(result);
	}	
	
	
}
