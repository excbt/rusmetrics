package ru.excbt.datafuse.nmk.data.model.vm;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Created by kovtonyk on 11.04.2017.
 */
@Getter
@Setter
public class EnergyPassportVM {

    private String templateKeyname;

    private String passportName;

    private String description;

    private Long organizationId;
}
