package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.service.ConsumptionService;
import ru.excbt.datafuse.nmk.service.ConsumptionTaskService;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTask;
import ru.excbt.datafuse.nmk.service.consumption.ConsumptionTaskTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/rma/consumption-task")
public class ConsumptionTaskResource {

    private static final Logger log = LoggerFactory.getLogger(ConsumptionTaskResource.class);

    private final ConsumptionTaskService consumptionTaskService;

    @Autowired
    public ConsumptionTaskResource(ConsumptionTaskService consumptionTaskService) {
        this.consumptionTaskService = consumptionTaskService;
    }


    @GetMapping("/list")
    @ApiOperation("Get queue of active tasks")
    @Timed
    public ResponseEntity<?> getList() {
        List<ConsumptionTask> consumptionTasks = consumptionTaskService.viewTaskQueue();
        return ResponseEntity.ok(consumptionTasks);
    }


    @GetMapping("/new")
    @ApiOperation("")
    @Timed
    public ResponseEntity<?> getNew(@RequestParam(name = "date", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> date,
                                    @RequestParam(name = "fromDateStr", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> fromDate,
                                    @RequestParam(name = "toDateStr", required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> toDate) {

        List<LocalDate> taskDates = new ArrayList<>();

        log.info("date exists: {}" ,date.isPresent());

        if (date.isPresent()) {
            taskDates.add(date.get());
        } else if (fromDate.isPresent() && toDate.isPresent()) {
            LocalDate day = fromDate.get();
            LocalDate date2 = toDate.get();
            while (day.isBefore(date2) || day.isEqual(date2)) {
                taskDates.add(day);
                day = day.plusDays(1);
            }
        }

        List<ConsumptionTask> taskList = processDateList(
            taskDates,
            EnumSet.allOf(ConsumptionService.DataType.class));

        return ResponseEntity.ok(taskList);
    }


    private List<ConsumptionTask> processDateList(List<LocalDate> taskDates, EnumSet<ConsumptionService.DataType> dataTypes) {
        List<ConsumptionTask> taskList = new ArrayList<>();

        for (LocalDate day : taskDates) {

            dataTypes.forEach(dt -> {
                ConsumptionTask task = ConsumptionTask.dayBuilder(day)
                    .dataType(dt.getKeyname())
                    .template(ConsumptionTaskTemplate.Template24H_from_1H)
                    .retryCnt(3).build().generateTaskUUID();
                consumptionTaskService.sendTask(task);
                taskList.add(task);
            });
        }
        return taskList;
    }


    /**
     *
     * @param year
     * @param mon
     * @return
     */
    @PutMapping("/new/month")
    @Timed
    @ApiOperation("Generates tasks for specified month")
    public ResponseEntity<?> putMonth(@RequestParam(name = "year") Integer year,
                                      @RequestParam(name = "mon") Integer mon) {

        LocalDate fromDate = LocalDate.of(year, mon, 1);
        LocalDate date2 = fromDate.plusMonths(1).minusDays(1);

        List<LocalDate> taskDates = new ArrayList<>();
        LocalDate day = fromDate;
        while (day.isBefore(date2) || day.isEqual(date2)) {
            taskDates.add(day);
            day = day.plusDays(1);
        }

        List<ConsumptionTask> taskList = processDateList(
            taskDates,
            EnumSet.allOf(ConsumptionService.DataType.class));

        return ResponseEntity.ok(taskList);
    }


    /**
     *
     * @param year
     * @return
     */
    @PutMapping("/new/year")
    @Timed
    @ApiOperation("Generates tasks for year")
    public ResponseEntity<?> putYear(@RequestParam(name = "year") Integer year) {

        LocalDate fromDate = LocalDate.of(year, 1, 1);
        LocalDate date2 = fromDate.plusYears(1).minusDays(1);

        List<LocalDate> taskDates = new ArrayList<>();
        LocalDate day = fromDate;
        while (day.isBefore(date2) || day.isEqual(date2)) {
            taskDates.add(day);
            day = day.plusDays(1);
        }

        List<ConsumptionTask> taskList = processDateList(
            taskDates,
            EnumSet.allOf(ConsumptionService.DataType.class));


        return ResponseEntity.ok(taskList);
    }
}
