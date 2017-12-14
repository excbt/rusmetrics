package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContZPointDeviceHistoryDTO {

    private Long contZPointId;

    private DeviceObjectShortInfoDTO deviceObject;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer revision = 1;

}
