package ru.excbt.datafuse.nmk.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

import javax.persistence.*;

/**
 * Created by kovtonyk on 10.04.2017.
 */

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport_section")
@Getter
@Setter
public class EnergyPassportSection extends JsonAbstractAuditableModel {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "passport_id")
    private EnergyPassport passport;

    @Column(name = "section_key")
    private String sectionKey;

    @Column(name = "section_json")
    @Type(type = "StringJsonObject")
    private String sectionJson;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

}
