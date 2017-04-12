package ru.excbt.datafuse.nmk.data.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * Created by kovtonyk on 12.04.2017.
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "energy_passport_data_value")
@Getter
@Setter
public class EnergyPassportDataValue implements Serializable {

    @EmbeddedId
    private EnergyPassportDataId energyPassportDataId;

    @Column(name = "data_value")
    private String dataValue;

    @Column(name = "data_type")
    private String dataType;

}
