package ru.excbt.datafuse.nmk.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by kovtonyk on 10.04.2017.
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport")
@Getter
@Setter
public class EnergyPassport extends JsonAbstractAuditableModel {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "passport_template_id")
    private EnergyPassportTemplate passportTemplate;

    @Column(name = "passport_date")
    private LocalDate passportDate;

    @Column(name = "description")
    private String description;

    @Column(name = "comment")
    private String comment;

    @Column(name = "organization_id")
    private Long organizationId;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "passport", cascade = CascadeType.ALL)
    private List<EnergyPassportSection> sections = new ArrayList<>();

}
