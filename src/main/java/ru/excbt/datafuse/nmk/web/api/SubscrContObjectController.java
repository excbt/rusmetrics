package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;


@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContObjectController {

	private final static int TEST_USER_ID = 729;
	
	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContObjectController.class);
	
	@Autowired
	private ContObjectService contObjectService;
	
	@Autowired
	private SubscrUserService subscrUserService;
	
	
	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = WebApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll() {
		logger.debug("Fire listAll");
		
		List<ContObject> resultList = subscrUserService.selectSubscrContObjects(TEST_USER_ID);
		
		return ResponseEntity.ok().body(resultList);
	}
	
}
