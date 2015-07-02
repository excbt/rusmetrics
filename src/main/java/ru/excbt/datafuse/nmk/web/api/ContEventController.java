package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.service.ContEventService;
import ru.excbt.datafuse.nmk.data.service.ContEventTypeService;

@Controller
@RequestMapping(value = "/api/contEvent")
public class ContEventController extends WebApiController {

	@Autowired
	private ContEventService contEventService;
	
	@Autowired
	private ContEventTypeService contEventTypeService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/types", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContEventTypes() {
		List<ContEventType> vList = contEventTypeService.selectBaseContEventTypes();
		return ResponseEntity.ok(vList);
	}
	
	

}
