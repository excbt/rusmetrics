package ru.excbt.datafuse.nmk.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.excbt.datafuse.nmk.data.model.support.LocalDateTimePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.utils.DateInterval;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class ConsumptionTask implements Serializable {

    public static final String CONS_TASK_QUEUE = "CONS_TASK_QUEUE";
    public static final int DEFAULT_RETRY = 3;

    private String name;

    private String srcTimeDetailType;

    private String destTimeDetailType;

    private String contServiceType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long contZPointId;

    private final int retryCnt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTimeFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTimeTo;

    @JsonIgnore
    public ConsumptionTask makeRetry() {
        return ConsumptionTask.builder()
            .name(this.name)
            .srcTimeDetailType(this.srcTimeDetailType)
            .destTimeDetailType(this.destTimeDetailType)
            .contServiceType(this.contServiceType)
            .dateTimeFrom(this.dateTimeFrom)
            .dateTimeTo(this.dateTimeTo)
            .retryCnt(this.retryCnt > 0 ? this.retryCnt - 1 : 0)
            .build();
    }

    @JsonIgnore
    public DateInterval toDateInterval() {
        return LocalDateTimePeriod.builder().dateTimeFrom(this.dateTimeFrom).dateTimeTo(this.dateTimeTo).build();
    }

    @JsonIgnore
    public boolean isValid() {
        return TimeDetailKey.searchKeyname(srcTimeDetailType) != null &&
            TimeDetailKey.searchKeyname(destTimeDetailType) != null &&
            ContServiceTypeKey.searchKeyname(contServiceType) != null && toDateInterval().isValid();
    }
}
