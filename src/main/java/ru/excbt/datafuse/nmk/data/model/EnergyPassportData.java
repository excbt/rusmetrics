package ru.excbt.datafuse.nmk.data.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import ru.excbt.datafuse.nmk.data.domain.DTOModel;
import ru.excbt.datafuse.nmk.data.domain.DTOUpdatableModel;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.dto.EnergyPassportDataDTO;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;
import ru.excbt.datafuse.nmk.data.model.modelmapper.ModelMapperUtil;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by kovtonyk on 10.04.2017.
 */

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport_data")
@Getter
@Setter
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EnergyPassportData extends JsonAbstractAuditableModel implements DeletedMarker, DTOModel<EnergyPassportDataDTO>,
    DTOUpdatableModel<EnergyPassportDataDTO> {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "passport_id")
    private EnergyPassport passport;

    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "section_key")
    private String sectionKey;

    @Column(name = "section_data_json")
    @Type(type = "StringJsonObject")
    private String sectionDataJson;

    @NotNull
    @Column(name = "section_entry_id")
    private Long sectionEntryId;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @Override
    public EnergyPassportDataDTO getDTO() {
        return ModelMapperUtil.map(this, EnergyPassportDataDTO.class);
    }

    @Override
    public void updateFromDTO(EnergyPassportDataDTO dto) {
        if (this.sectionKey != null && !this.sectionKey.equals(dto.getSectionKey())) {
            throw new IllegalArgumentException();
        }
        if (this.deleted == 1) {
            throw new IllegalStateException();
        }
        this.sectionDataJson = dto.getSectionDataJson();
        this.sectionKey = dto.getSectionKey();
        this.sectionEntryId = dto.getSectionEntryId();
        this.version = dto.getVersion();
    }
}
