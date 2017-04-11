package ru.excbt.datafuse.nmk.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportTemplateDTO;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;
import ru.excbt.datafuse.nmk.data.model.modelmapper.ModelMapperUtil;

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
public class EnergyPassport extends JsonAbstractAuditableModel implements DeletedMarker {


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "passport_template_id")
    private EnergyPassportTemplate passportTemplate;

    @Column(name = "passport_name")
    private String passportName;

    @Column(name = "passport_date")
    private LocalDate passportDate;

    @Column(name = "description")
    private String description;

    @Column(name = "comment")
    private String comment;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "passport", cascade = CascadeType.ALL)
    private List<EnergyPassportSection> sections = new ArrayList<>();

    public void addSection(EnergyPassportSection energyPassportSection) {
        energyPassportSection.setPassport(this);
        this.sections.add(energyPassportSection);
    }

    @JsonIgnore
    public EnergyPassportDTO getDTO() {
        return ModelMapperUtil.map(this, EnergyPassportDTO.class);
    }

}
