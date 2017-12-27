package ru.excbt.datafuse.nmk.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.SubscrContServiceDataHWaterResource;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;

/**
 * Created by kovtonyk on 31.05.2017.
 */
@RestController
@RequestMapping(value = "/api/subscr")
public class ContServiceDataSubscrResource {

    private final SubscrContServiceDataHWaterResource subscrContServiceDataHWaterController;

    @Autowired
    public ContServiceDataSubscrResource(SubscrContServiceDataHWaterResource subscrContServiceDataHWaterController) {
        this.subscrContServiceDataHWaterController = subscrContServiceDataHWaterController;
    }


    @RequestMapping(value = "/{contObjectId}/u-service/{timeDetailType}/{contZPointId}", method = RequestMethod.GET,
        produces = ApiConst.APPLICATION_JSON_UTF8)
    public ResponseEntity<?> getDataHWater(@PathVariable("contObjectId") long contObjectId,
                                           @PathVariable("contZPointId") long contZPointId,
                                           @PathVariable("timeDetailType") String timeDetailType,
                                           @RequestParam("beginDate") String fromDateStr,
                                           @RequestParam("endDate") String toDateStr) {

        ResponseEntity<?> result = subscrContServiceDataHWaterController.getDataHWater(contObjectId, contZPointId, timeDetailType, fromDateStr, toDateStr);

        result.getHeaders().add("ui-type","h-water");
        return result;
    }

}
