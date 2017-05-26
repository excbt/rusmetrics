package ru.excbt.datafuse.nmk.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import ru.excbt.datafuse.nmk.data.domain.DTOModel;
import ru.excbt.datafuse.nmk.data.domain.DTOUpdatableModel;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportSectionEntryDTO;
import ru.excbt.datafuse.nmk.data.model.modelmapper.ModelMapperUtil;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * Created by kovtonyk on 17.04.2017.
 */

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport_section_entry")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EnergyPassportSectionEntry extends JsonAbstractAuditableModel implements Serializable,
DTOModel<EnergyPassportSectionEntryDTO>, DTOUpdatableModel<EnergyPassportSectionEntryDTO> {

    @NotNull
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "section_id")
    private EnergyPassportSection section;

    @Column(name = "entry_name")
    private String entryName;

    @Column(name = "entry_description")
    private String entryDescription;

    @Column(name = "entry_order")
    private Integer entryOrder;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @Override
    public EnergyPassportSectionEntryDTO getDTO() {
        return ModelMapperUtil.map(this, EnergyPassportSectionEntryDTO.class);
    }

    @Override
    public void updateFromDTO(EnergyPassportSectionEntryDTO dto) {
        this.entryName = dto.getEntryName();
        this.entryDescription = dto.getEntryDescription();
        this.entryOrder = dto.getEntryOrder();
    }
}
