package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
public class ContZPointAccessDTO {

    private Long contZPointId;

    private Long subscriberId;

    private String contServiceTypeKeyname;

    private Integer contZPointTsNumber;

    private String contZPointCustomServiceName;

    private String accessType;

    private LocalDateTime accessTtl;

    private ZonedDateTime grantTZ;

}
