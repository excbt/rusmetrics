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

    private boolean hasEntries;

    private int version;

    public EnergyPassportSectionTemplateDTO hasEntries(boolean value) {
        this.hasEntries = value;
        return this;
    }

    @Override
    public String toString() {
        return "EnergyPassportSectionTemplateDTO{" +
            "id=" + id +
            ", passportTemplateId=" + passportTemplateId +
            ", sectionKey='" + sectionKey + '\'' +
            ", sectionJson='" + sectionJson + '\'' +
            ", sectionOrder=" + sectionOrder +
            ", hasEntries=" + hasEntries +
            ", version=" + version +
            '}';
    }
}
