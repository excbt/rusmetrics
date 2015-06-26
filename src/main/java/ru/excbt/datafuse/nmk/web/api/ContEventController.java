package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.service.ContEventService;

@Controller
@RequestMapping(value = "/api/contEvent")
public class ContEventController extends WebApiController {

	@Autowired
	private ContEventService contEventService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/types", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContEventTypes() {
		List<ContEventType> vList = contEventService.findAllContEventTypes();
		return ResponseEntity.ok(vList);
	}
	
	

}
