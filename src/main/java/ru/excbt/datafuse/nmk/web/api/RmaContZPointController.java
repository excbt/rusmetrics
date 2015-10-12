package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;

@Controller
@RequestMapping(value = "/api/rma")
public class RmaContZPointController extends SubscrContZPointController {

	private static final Logger logger = LoggerFactory.getLogger(RmaContZPointController.class);

	/**
	 * 
	 */
	@Override
	@RequestMapping(value = "/contObjects/{contObjectId}/zpoints/{contZPointId}", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContZPoint(@PathVariable("contObjectId") long contObjectId,
			@PathVariable("contZPointId") long contZPointId, @RequestBody ContZPoint contZPoint) {
		return responseBadRequest();
	}

}
