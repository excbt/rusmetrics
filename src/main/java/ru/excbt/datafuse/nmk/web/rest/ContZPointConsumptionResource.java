package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.support.time.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.time.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.support.time.LocalDateTimePeriod;
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

    @GetMapping("/{contZPointId}/paged")
    @ApiOperation("Get list of supported service types")
    @Timed
    public ResponseEntity<?> getConsumptionDataPaged(@PathVariable("contZPointId") final Long contZPointId,
                                                    @RequestParam(name = "timeDetailKeyname", required = false) final String timeDetailKeyname,
                                                    @RequestParam(name = "fromDateStr", required = false) String fromDateStr,
                                                    @RequestParam(name = "toDateStr", required = false) String toDateStr,
                                                    @PageableDefault(value = 50) Pageable pageable) {


        String searchTimeDetailKeyname = timeDetailKeyname == null ? TimeDetailKey.TYPE_24H.getKeyname() : timeDetailKeyname;

        Optional<TimeDetailKey> timeDetailKeyOptional = KeyEnumTool.searchKey(TimeDetailKey.class, searchTimeDetailKeyname);
        if (!timeDetailKeyOptional.isPresent()) {
            return ApiResponse.responseBadRequest();
        }

        if ((fromDateStr == null) ^ (toDateStr == null)) {
            return ResponseEntity.badRequest().body(String
                .format("Invalid parameters fromDateStr:{} and toDateStr:{}. Must be both null or not null", fromDateStr, toDateStr));
        }

        Optional<LocalDatePeriod> localDatePeriod = LocalDatePeriodParser.parse(fromDateStr, toDateStr);

        if (localDatePeriod.isPresent() && localDatePeriod.get().isInvalidEq()) {
            return ResponseEntity.badRequest().body(String
                .format("Invalid parameters fromDateStr:{} is greater than toDateStr:{}", fromDateStr, toDateStr));
        }

        Page<ContZPointConsumptionDTO> resultList = contZPointConsumptionService.getConsumptionDataPaged(
            contZPointId,
            timeDetailKeyOptional.get(),
            localDatePeriod.map(LocalDatePeriod::toLocalDateTimePeriod).orElse(LocalDateTimePeriod.currentMonth()),
            pageable);
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
