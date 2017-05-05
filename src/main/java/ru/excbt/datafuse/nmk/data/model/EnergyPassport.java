package ru.excbt.datafuse.nmk.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import ru.excbt.datafuse.nmk.data.domain.DTOModel;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportShortDTO;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;
import ru.excbt.datafuse.nmk.data.model.modelmapper.ModelMapperUtil;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;


/**
 * Created by kovtonyk on 10.04.2017.
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EnergyPassport extends JsonAbstractAuditableModel implements DeletedMarker, DTOModel<EnergyPassportDTO> {

    public static final int DEFAULT_DOCUMENT_MODE = 1;

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

    @NotNull
    @Column(name = "document_mode")
    private Integer documentMode = DEFAULT_DOCUMENT_MODE;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "passport", cascade = CascadeType.ALL)
    private List<EnergyPassportSection> sections = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "energy_passport_cont_object", schema = DBMetadata.SCHEME_PORTAL, //
        joinColumns = @JoinColumn(name = "passport_id"), //
        inverseJoinColumns = @JoinColumn(name = "cont_object_id"))
    @JsonIgnore
    @Getter
    @Setter
    private Set<ContObject> contObjects = new HashSet<>();

    public void addSection(EnergyPassportSection energyPassportSection) {
        energyPassportSection.setPassport(this);
        this.sections.add(energyPassportSection);
    }

    @Override
    public EnergyPassportDTO getDTO() {
        EnergyPassportDTO result = ModelMapperUtil.map(this, EnergyPassportDTO.class);
        if (!result.getSections().stream().filter((i) -> i.getSectionOrder() == null).findFirst().isPresent()) {
            result.getSections().sort(Comparator.comparingInt(EnergyPassportSectionDTO::getSectionOrder));
        }
        return result;
    }

    public EnergyPassportShortDTO getDTO_Short() {
        EnergyPassportShortDTO result = ModelMapperUtil.map(this, EnergyPassportShortDTO.class);
        return result;
    }

    @JsonIgnore
    public Optional<EnergyPassportSection> searchSection(String sectionKey) {
        if (sectionKey == null) {
            return Optional.empty();
        }
        return sections.stream().filter(i -> sectionKey.equals(i.getSectionKey())).findFirst();
    }

    public EnergyPassport id (Long id) {
        this.setId(id);
        return this;
    }

}
