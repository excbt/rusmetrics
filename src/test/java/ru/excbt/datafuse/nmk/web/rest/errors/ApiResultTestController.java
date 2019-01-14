package ru.excbt.datafuse.nmk.web.rest.errors;

import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionVoidProcess;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

@RestController
public class ApiResultTestController {

    @GetMapping("/test/api-param-exception")
    public ResponseEntity<?> apiParamException() {
        ApiActionVoidProcess action = () -> {throw new IllegalArgumentException();};
        return ApiResponse.responseOK(action);
    }

    @GetMapping("/test/api-param-to-vm")
    public ResponseEntity<?> apiParamToVmException() {
        ApiActionVoidProcess action = () -> {throw new IllegalArgumentException();};
        return ApiResponse.responseOK(action);
    }


}
