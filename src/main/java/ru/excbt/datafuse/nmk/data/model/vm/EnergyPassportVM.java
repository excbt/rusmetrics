package ru.excbt.datafuse.nmk.data.model.vm;

import lombok.*;

import java.time.LocalDate;

/**
 * Created by kovtonyk on 11.04.2017.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyPassportVM {

    private String templateKeyname;

    private String passportName;

    private String description;

    private Long organizationId;
}
