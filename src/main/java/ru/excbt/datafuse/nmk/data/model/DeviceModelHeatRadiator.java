package ru.excbt.datafuse.nmk.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by kovtonyk on 29.05.2017.
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_model_heat_radiator" )
@Getter
@Setter
public class DeviceModelHeatRadiator implements Serializable {

    @EmbeddedId
    private DeviceModelHeatRadiatorId deviceModelHeatRadiatorPK;

    @Column(name = "kc", columnDefinition = "numeric(12,4)", precision = 12, scale = 4 )
    private Double kc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceModelHeatRadiator that = (DeviceModelHeatRadiator) o;

        return deviceModelHeatRadiatorPK!= null ? deviceModelHeatRadiatorPK.equals(that.deviceModelHeatRadiatorPK) : that.deviceModelHeatRadiatorPK == null;
    }

    @Override
    public int hashCode() {
        return deviceModelHeatRadiatorPK != null ? deviceModelHeatRadiatorPK.hashCode() : 0;
    }
}
