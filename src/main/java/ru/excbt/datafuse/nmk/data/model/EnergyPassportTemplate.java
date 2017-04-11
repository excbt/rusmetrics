package ru.excbt.datafuse.nmk.data.model;

/**
 * Created by kovtonyk on 29.03.2017.
 */

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.opensaml.xml.signature.P;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate.BuilderInitializer;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionTemplateDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportTemplateDTO;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport_template")
@Getter
@Setter
public class EnergyPassportTemplate extends AbstractAuditableModel implements DeletedMarker {

    @Column(name = "keyname")
    private String keyname;

    @Column(name = "description")
    private String description;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "document_version")
    private String documentVersion;

    @Column(name = "document_date")
    private LocalDate documentDate;

    @Column(name = "deleted")
    private int deleted;

    @Version
    private int version;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "passportTemplate", cascade = CascadeType.ALL)
    private List<EnergyPassportSectionTemplate> sectionTemplates = new ArrayList<>();

    /**
     *
     * @param builderInitializer
     * @return
     */
    public EnergyPassportSectionTemplate createSection(BuilderInitializer builderInitializer) {
        EnergyPassportSectionTemplate.EnergyPassportSectionTemplateBuilder builder = EnergyPassportSectionTemplate.builder().passportTemplate(this);
        builderInitializer.init(builder);
        EnergyPassportSectionTemplate result = builder.build();
        sectionTemplates.add(result);
        return result;
    }

    public EnergyPassportSectionTemplate addSection(EnergyPassportSectionTemplate sectionTemplate) {
        sectionTemplates.add(sectionTemplate);
        sectionTemplate.setSectionOrder(sectionTemplates.size());
        return sectionTemplate;
    }


    public void updateFromDTO(EnergyPassportTemplateDTO dto) {
        if (this.keyname != null && !this.keyname.equals(dto.getKeyname())) {
            throw new IllegalArgumentException();
        }
        if (this.documentVersion != null && !this.documentVersion.equals(dto.getDocumentVersion())) {
            throw new IllegalArgumentException();
        }
        if (this.deleted == 1) {
            throw new IllegalStateException();
        }
        this.keyname = dto.getKeyname();
        this.description = dto.getDescription();
        this.documentDate = dto.getDocumentDate();
        this.documentName = dto.getDocumentName();
        this.documentVersion = dto.getDocumentVersion();
        //this.version = dto.getVersion() != null ? dto.getVersion() : 0;
        dto.getSectionTemplates().forEach(i -> addOrUpdateSection(i));
    }

    public void addOrUpdateSection(EnergyPassportSectionTemplateDTO sectionDTO) {
        Optional<EnergyPassportSectionTemplate> checkSection =
            sectionTemplates.stream()
                .filter(i -> i.getSectionKey() != null && i.getSectionKey().equals(sectionDTO.getSectionKey()))
                .findFirst();

        if (checkSection.isPresent()) {
            checkSection.get().updateFromDTO(sectionDTO);
        } else {
            EnergyPassportSectionTemplate sectionTemplate = new EnergyPassportSectionTemplate();
            sectionTemplate.updateFromDTO(sectionDTO);
            sectionTemplate.setPassportTemplate(this);
            sectionTemplates.add(sectionTemplate);
        }
    }

}
