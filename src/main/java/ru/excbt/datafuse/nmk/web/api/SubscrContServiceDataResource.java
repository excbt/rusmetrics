package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

/**
 * Created by kovtonyk on 01.06.2017.
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContServiceDataResource extends SubscrApiController {


    private final SubscrContServiceDataHWaterController subscrContServiceDataHWaterController;


    public SubscrContServiceDataResource(SubscrContServiceDataHWaterController subscrContServiceDataHWaterController) {
        this.subscrContServiceDataHWaterController = subscrContServiceDataHWaterController;
    }

    @RequestMapping(value = "/u-service/contObjects/importData", method = RequestMethod.POST,
        produces = APPLICATION_JSON_UTF8)
    public ResponseEntity<?> importDataMultipleFiles(@RequestParam("files") MultipartFile[] multipartFiles) {



        return subscrContServiceDataHWaterController.importDataHWaterMultipleFiles(multipartFiles);
    }

}
