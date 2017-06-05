package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;

/**
 * Created by kovtonyk on 31.05.2017.
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class ContServiceDataSubscrResource extends AbstractSubscrApiResource {

    private final SubscrContServiceDataHWaterController subscrContServiceDataHWaterController;

    public ContServiceDataSubscrResource(SubscrContServiceDataHWaterController subscrContServiceDataHWaterController) {
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
