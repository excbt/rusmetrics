package ru.excbt.datafuse.nmk.web.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping(value = "/rest/check")
public class RestCheckController {

	
	@RequestMapping(method = RequestMethod.GET, produces = RestConst.APPLICATION_JSON)
	public ResponseEntity<?> listAll() {
		return ResponseEntity.ok("Hallo. Check is OK. I am a JSON )))");
	}	
	
}
