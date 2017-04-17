package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@Getter
@Setter
public class EnergyPassportSectionDTO {

    private Long id;

    @NotNull
    private Long passportId;

    @NotNull
    private String sectionKey;

    @NotNull
    private String sectionJson;

    @NotNull
    private Integer sectionOrder;

    private boolean enabled = true;

    private boolean hasEntries = false;

    private int version;

    @Override
    public String toString() {
        return "EnergyPassportSectionDTO{" +
            "id=" + id +
            ", passportId=" + passportId +
            ", sectionKey='" + sectionKey + '\'' +
            ", sectionOrder=" + sectionOrder +
            ", hasEntries=" + hasEntries +
            ", version=" + version +
            '}';
    }
}
