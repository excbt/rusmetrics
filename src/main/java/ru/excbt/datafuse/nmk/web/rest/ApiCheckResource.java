package ru.excbt.datafuse.nmk.web.rest;

import io.swagger.annotations.ApiOperation;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.QueryProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.service.DBSessionService;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/check")
public class ApiCheckResource {

    private final DBSessionService dbSessionService;

    public ApiCheckResource(DBSessionService dbSessionService) {
        this.dbSessionService = dbSessionService;
    }

    @ApiOperation(value = "Check rest for russian lan suppirt")
    @RequestMapping(method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> listAll() {

        if (checkDB()) {
            return ResponseEntity.ok(ApiResult
                .ok("Hallo. Check is OK. I am a JSON. И я могу по РУССКИ )))"));
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service is unavailable");
        }

	}

	private boolean checkDB() {
        boolean result = true;
        try {
            List<?> qryResult = dbSessionService.getSession().createNativeQuery("select now()").list();
            result = qryResult.size() > 0;
        } catch (Exception e) {
            result = false;
        } finally {
        }

        return result;
    }

}
