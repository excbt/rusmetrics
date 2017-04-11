package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kovtonyk on 31.03.2017.
 */
@Getter
@Setter
public class EnergyPassportSectionTemplateDTO {

    private Long id;

    private Long passportTemplateId;

    private String sectionKey;

    private String sectionJson;

    private Integer sectionOrder;

    private Integer version;

}
