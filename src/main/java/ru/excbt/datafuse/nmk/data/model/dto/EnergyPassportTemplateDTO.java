package ru.excbt.datafuse.nmk.data.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kovtonyk on 30.03.2017.
 */
@Getter
@Setter
public class EnergyPassportTemplateDTO {

    private Long id;

    private String keyname;

    private String description;

    private String documentName;

    private String documentVersion;

    private LocalDate documentDate;

    private int version;

    private List<EnergyPassportSectionTemplateDTO> sectionTemplates = new ArrayList<>();

    public void addSection(EnergyPassportSectionTemplateDTO section) {
        this.sectionTemplates.add(section);
        section.setSectionOrder(sectionTemplates.size());
    }

    @Override
    public String toString() {
        return "EnergyPassportTemplateDTO{" +
            "id=" + id +
            ", keyname='" + keyname + '\'' +
            ", description='" + description + '\'' +
            ", documentName='" + documentName + '\'' +
            ", documentVersion='" + documentVersion + '\'' +
            ", documentDate=" + documentDate +
            ", version=" + version +
            ", sectionTemplates=" + sectionTemplates +
            '}';
    }
}
