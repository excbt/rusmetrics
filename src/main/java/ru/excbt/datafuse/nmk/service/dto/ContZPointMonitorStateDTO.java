package ru.excbt.datafuse.nmk.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContZPointMonitorStateDTO {

    private Long contZPointId;

    private String contServiceTypeKeyname;

    private String customServiceName;

    private String tsNumber;

    private String stateColor;

}
