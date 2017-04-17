package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by kovtonyk on 17.04.2017.
 */
@Getter
@Setter
public class EnergyPassportSectionEntryDTO {

    private Long id;

    @NotNull
    private Long sectionId;

    @NotNull
    private String entryName;

    private String entryDescription;

    private Integer entryOrder;

    private int version;

    @Override
    public String toString() {
        return "EnergyPassportSectionEntryDTO{" +
            "id=" + id +
            ", sectionId=" + sectionId +
            ", entryName='" + entryName + '\'' +
            ", entryDescription='" + entryDescription + '\'' +
            ", entryOrder=" + entryOrder +
            ", version=" + version +
            '}';
    }
}
