package ru.excbt.datafuse.nmk.data.model;

/**
 * Created by kovtonyk on 29.03.2017.
 */

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate.BuilderInitializer;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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

}
