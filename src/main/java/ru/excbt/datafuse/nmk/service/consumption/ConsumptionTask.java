package ru.excbt.datafuse.nmk.service.consumption;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.excbt.datafuse.nmk.data.model.support.InstantPeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.utils.DateInterval;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Builder
@AllArgsConstructor
@ToString
public class ConsumptionTask implements Serializable {

    public static final String CONS_TASK_QUEUE = "CONS_TASK_QUEUE";
    public static final int DEFAULT_RETRY = 3;

    @Getter
    private final UUID taskUUID;

    @Getter
    private final String name;

    @Getter
    private final ConsumptionTaskTemplate template;

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long contZPointId;

    @Getter
    private final int retryCnt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Getter
    @Setter
    private LocalDate dateFrom;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Getter
    @Setter
    private LocalDate dateTo;

    @Getter
    private final String dataType;

    @JsonIgnore
    public ConsumptionTask makeRetry() {
        return ConsumptionTask.builder()
            .name(this.name)
            .template(this.template)
            .dateFrom(this.dateFrom)
            .dateTo(this.dateTo)
            .retryCnt(this.retryCnt > 0 ? this.retryCnt - 1 : 0)
            .build();
    }

    @JsonIgnore
    public ConsumptionTask newTaskUUID(UUID taskUUID) {

        if (this.taskUUID != null)
            return this;

        return ConsumptionTask.builder()
            .name(this.name)
            .template(this.template)
            .dateFrom(this.dateFrom)
            .dateTo(this.dateTo)
            .retryCnt(this.retryCnt)
            .taskUUID(taskUUID)
            .build();
    }

    @JsonIgnore
    public DateInterval toDateInterval() {
        return InstantPeriod.builder().dateTimeFrom(startOfDay(this.dateFrom)).dateTimeTo(endOfDay(this.dateTo)).build();
    }

    @JsonIgnore
    public boolean isValid() {
        return TimeDetailKey.searchKeyname(template.getSrcTimeDetailType()) != null &&
            TimeDetailKey.searchKeyname(template.getDestTimeDetailType()) != null &&
            //ContServiceTypeKey.searchKeyname(contServiceType) != null &&
            toDateInterval().isValid();
    }


    @JsonIgnore
    public String getSrcTimeDetailType() {
        return template != null ? this.template.getSrcTimeDetailType() : null;
    }

    @JsonIgnore
    public String getDestTimeDetailType() {
        return template != null ? this.template.getDestTimeDetailType() : null;
    }

    public String getDataType() {
        return this.dataType;
    }

    @JsonIgnore
    public Instant getDateTimeFrom() {
        return startOfDay(dateFrom);
    }

    @JsonIgnore
    public Instant getDateTimeTo() {
        return endOfDay(dateTo);
    }

    private Instant endOfDay(LocalDate date) {
        return date.atStartOfDay().plusDays(1).minusSeconds(1).atZone(ZoneId.systemDefault()).toInstant();
    }

    private Instant startOfDay(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    @JsonIgnore
    public long getDaysBetween() {
        return ChronoUnit.DAYS.between(dateFrom, dateTo) + 1;
    }

    public ConsumptionTask checkDaysBetween(int days) {
        if (getDaysBetween() != days) {
            throw new IllegalStateException("Days duration check failed. Expected:" + days + ", actual:" + getDaysBetween());
        }
        return this;
    }

    public static ConsumptionTask dayConsumptionTask(ConsumptionTaskTemplate template, LocalDate date) {
        return ConsumptionTask.builder().template(template).dateFrom(date).dateTo(date).build();
    }

}
