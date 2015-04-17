package ru.excbt.datafuse.nmk.web.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.web.api.WebApiConst;


@Controller
@RequestMapping(value = "/rest/check")
public class RestCheckController {

	
	@RequestMapping(method = RequestMethod.GET, produces = WebApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll() {
		return ResponseEntity.ok("Hallo. Check is OK. I am a JSON. И я могу по РУССКИ )))");
	}	
	
}
