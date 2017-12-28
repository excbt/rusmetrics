package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;

@RestController
@RequestMapping(value = "/rest/check")
public class ApiCheckResource {

    @GetMapping
	//@RequestMapping(method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
    @ApiOperation(value = "Check rest for russian lan suppirt")
	public ResponseEntity<?> listAll() {
		return ResponseEntity.ok(ApiResult
				.ok("Hallo. Check is OK. I am a JSON. И я могу по РУССКИ )))"));
	}

}
