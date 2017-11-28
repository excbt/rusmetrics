package ru.excbt.datafuse.nmk.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.domain.DTOUpdatableModel;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionTemplateDTO;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

import javax.persistence.*;

/**
 * Created by kovtonyk on 31.03.2017.
 */

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport_section_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyPassportSectionTemplate extends AbstractAuditableModel implements DeletedMarker, DTOUpdatableModel<EnergyPassportSectionTemplateDTO> {

    public interface BuilderInitializer {
        void init(EnergyPassportSectionTemplateBuilder sectionBuilder);
    }

    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "passport_template_id", updatable = false)
    private EnergyPassportTemplate passportTemplate;

    @Column(name = "section_key")
    private String sectionKey;


    @Column(name = "section_json")
    @Type(type = "JsonbAsString")
    private String sectionJson;

    @Column(name = "section_order")
    private Integer sectionOrder;

    @Column(name = "has_entries")
    private boolean hasEntries;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @Override
    public void updateFromDTO(EnergyPassportSectionTemplateDTO dto) {
        if (this.sectionKey != null && !this.sectionKey.equals(dto.getSectionKey())) {
            throw new IllegalArgumentException();
        }
        if (this.deleted == 1) {
            throw new IllegalStateException();
        }

        this.sectionKey = dto.getSectionKey();
        this.sectionJson = dto.getSectionJson();
        this.sectionOrder = dto.getSectionOrder();
        this.hasEntries = dto.isHasEntries();
        //this.version = dto.getVersion();
    }


    public EnergyPassportSectionTemplate hasEntries(boolean value) {
        this.hasEntries = value;
        return this;
    }

}
