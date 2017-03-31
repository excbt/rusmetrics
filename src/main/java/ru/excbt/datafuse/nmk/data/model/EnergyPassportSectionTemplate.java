package ru.excbt.datafuse.nmk.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
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
public class EnergyPassportSectionTemplate extends AbstractAuditableModel implements DeletedMarker {

    public interface BuilderInitializer {
        void init(EnergyPassportSectionTemplateBuilder sectionBuilder);
    }

    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "passport_template_id")
    private EnergyPassportTemplate passportTemplate;

    @Column(name = "section_key")
    private String sectionKey;


    @Column(name = "section_json")
    @Type(type = "StringJsonObject")
    private String sectionJson;

    @Column(name = "section_order")
    private Integer sectionOrder;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

}
