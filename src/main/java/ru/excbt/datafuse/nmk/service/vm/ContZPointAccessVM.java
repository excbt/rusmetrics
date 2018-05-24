package ru.excbt.datafuse.nmk.service.vm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public class ContZPointAccessVM {

    private Long contZPointId;

    private Long subscriberId;

    private String contServiceTypeKeyname;

    private Integer contZPointTsNumber;

    private String contZPointCustomServiceName;

    private String accessType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime accessTtl;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime grantTz;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime revokeTz;

    private boolean accessEnabled;
}
