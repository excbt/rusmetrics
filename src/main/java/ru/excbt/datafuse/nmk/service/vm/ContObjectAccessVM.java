package ru.excbt.datafuse.nmk.service.vm;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public class ContObjectAccessVM {

    private Long contObjectId;

    private Long subscriberId;

    private String contObjectName;

    private String contObjectFullName;

    private String contObjectFullAddress;

    private String contObjectNumber;

    private String accessType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime accessTtl;

    private boolean accessEnabled;

    private int allContZPointCnt;

    private int accessContZPointCnt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime grantTz;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime revokeTz;

}
