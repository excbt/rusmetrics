package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.domain.tools.KeyEnumTool;
import ru.excbt.datafuse.nmk.service.ContZPointConsumptionDTO;
import ru.excbt.datafuse.nmk.service.ContZPointConsumptionService;
import ru.excbt.datafuse.nmk.service.vm.ContZPointConsumptionVM;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/subscr/cont-zpoint-consumption")
public class ContZPointConsumptionResource {

    private final ContZPointConsumptionService contZPointConsumptionService;


    @Autowired
    public ContZPointConsumptionResource(ContZPointConsumptionService contZPointConsumptionService) {
        this.contZPointConsumptionService = contZPointConsumptionService;
    }

    @GetMapping("/{contZPointId}")
    @ApiOperation("Get list of supported service types")
    @Timed
    public ResponseEntity<?> getServiceTypes(@PathVariable("contZPointId") final Long contZPointId,
                                             @RequestParam(name = "timeDetailKeyname", required = false) final String timeDetailKeyname) {

        String searchTimeDetailKeyname = timeDetailKeyname == null ? TimeDetailKey.TYPE_24H.getKeyname() : timeDetailKeyname;

        Optional<TimeDetailKey> timeDetailKeyOptional = KeyEnumTool.searchKey(TimeDetailKey.class, searchTimeDetailKeyname);
        if (!timeDetailKeyOptional.isPresent()) {
            return ApiResponse.responseBadRequest();
        }

        List<ContZPointConsumptionDTO> resultList = contZPointConsumptionService.getConsumption(contZPointId,
            timeDetailKeyOptional.get(), LocalDateTimePeriod.month(2017,2));
        return ResponseEntity.ok(resultList);
    }

    /**
     *
     * @param yyyy
     * @return
     */
    @GetMapping("/year/{yyyy}")
    @ApiOperation("Get list of supported service types")
    @Timed
    public ResponseEntity<?> getConsumptionAvailYear(@PathVariable("yyyy") final Integer yyyy) {


        if (yyyy < 2010 || yyyy > 2100) {
            return ApiResponse.responseBadRequest(ApiResult.validationError("Year is invalid"));
        }

        List<ContZPointConsumptionVM> availDataVMList = contZPointConsumptionService.findAvailableYearData(LocalDateTimePeriod.year(yyyy));

        return ResponseEntity.ok(availDataVMList);

    }

    @GetMapping("/year/{yyyy}/mon/{mon}")
    @ApiOperation("Get list of supported service types")
    @Timed
    public ResponseEntity<?> getConsumptionAvailMon(@PathVariable("yyyy") final Integer yyyy, @PathVariable("mon") final Integer mon) {


        if (yyyy < 2010 || yyyy > 2100) {
            return ApiResponse.responseBadRequest(ApiResult.validationError("Year is invalid"));
        }

        List<ContZPointConsumptionVM> availDataVMList = contZPointConsumptionService.findAvailableMonthData(yyyy, mon);

        return ResponseEntity.ok(availDataVMList);

    }

}
