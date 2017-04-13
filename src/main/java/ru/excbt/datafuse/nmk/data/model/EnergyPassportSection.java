package ru.excbt.datafuse.nmk.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by kovtonyk on 10.04.2017.
 */

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport_section")
@Getter
@Setter
public class EnergyPassportSection extends JsonAbstractAuditableModel {

    @NotNull
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "passport_id")
    private EnergyPassport passport;

    @NotNull
    @Column(name = "section_key")
    private String sectionKey;

    @NotNull
    @Column(name = "section_json")
    @Type(type = "StringJsonObject")
    private String sectionJson;

    @NotNull
    @Column(name = "section_order")
    private Integer sectionOrder;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    public void updateFromTemplate (EnergyPassportSectionTemplate template) {
        this.sectionKey = template.getSectionKey();
        this.sectionJson = template.getSectionJson();
        this.sectionOrder = template.getSectionOrder();
    }
}
