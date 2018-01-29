package ru.excbt.datafuse.nmk.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContZPointConsumptionDTO {

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private Long contZPointId;

    private LocalDateTime consDateTime;

    private Double consValue;

    private String consValueName;

}
