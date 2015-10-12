package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContObject;

// TODO make RMA actions
@Controller
@RequestMapping(value = "/api/rma")
public class RmaContObjectController extends SubscrContObjectController {

	private static final Logger logger = LoggerFactory.getLogger(RmaContObjectController.class);

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createContObject(@RequestBody ContObject contObject) {

		return responseBadRequest();
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteContObject(@RequestBody ContObject contObject,
			@PathVariable("contObjectId") Long contObjectId) {

		return responseBadRequest();
	}

}
