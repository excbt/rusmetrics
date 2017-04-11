package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 05.04.2017.
 */
@Getter
@Setter
public class EnergyPassportDataDTO {

    private Long id;

    private Long passportId;

    private String sectionKey;

    private String sectionDataJson;

    private int version;
}
