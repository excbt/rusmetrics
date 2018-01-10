package ru.excbt.datafuse.nmk.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

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

    private String timeDetailType;

    private String contServiceType;

    private final int retryCnt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTimeFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTimeTo;


    public ConsumptionTask makeRetry() {
        return ConsumptionTask.builder()
            .name(this.name)
            .timeDetailType(this.timeDetailType)
            .contServiceType(this.contServiceType)
            .dateTimeFrom(this.dateTimeFrom)
            .dateTimeTo(this.dateTimeTo)
            .retryCnt(this.retryCnt > 0 ? this.retryCnt - 1 : 0)
            .build();
    }


}
