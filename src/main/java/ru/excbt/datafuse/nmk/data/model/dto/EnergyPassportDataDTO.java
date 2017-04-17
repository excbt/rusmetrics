package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by kovtonyk on 05.04.2017.
 */
@Getter
@Setter
public class EnergyPassportDataDTO {

    private Long id;

    @NotNull
    private Long passportId;

    @NotNull
    private String sectionKey;

    @Min(0)
    private long sectionEntryId = 0;

    private String sectionDataJson;

    private int version;

    @Override
    public String toString() {
        return "EnergyPassportDataDTO{" +
            "id=" + id +
            ", passportId=" + passportId +
            ", sectionKey='" + sectionKey + '\'' +
            ", sectionEntryId=" + sectionEntryId +
            ", sectionDataJson='" + sectionDataJson + '\'' +
            ", version=" + version +
            '}';
    }
}
