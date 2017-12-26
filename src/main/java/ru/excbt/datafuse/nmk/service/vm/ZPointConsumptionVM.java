package ru.excbt.datafuse.nmk.service.vm;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ZPointConsumptionVM {

    private Long contZPointId;

    private String contServiceType;

    private LocalDateTime fromDateTime;

    private LocalDateTime toDateTime;

    private Double consValue;

}
