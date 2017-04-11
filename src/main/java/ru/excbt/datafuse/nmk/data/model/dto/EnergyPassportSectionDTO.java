package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@Getter
@Setter
public class EnergyPassportSectionDTO {

    private Long id;

    private Long passportId;

    private String sectionKey;

    private String sectionJson;

    private Integer sectionOrder;

    private int version;
}
