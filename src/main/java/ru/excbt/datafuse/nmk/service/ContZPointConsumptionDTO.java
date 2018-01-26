package ru.excbt.datafuse.nmk.service;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ContZPointConsumptionDTO {

    private Long id;

    private Long contZPointId;

    private LocalDateTime consDateTime;

    private Double consValue;

    private String consValueName;

}
