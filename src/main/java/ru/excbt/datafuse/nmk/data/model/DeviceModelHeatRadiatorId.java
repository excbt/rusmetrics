package ru.excbt.datafuse.nmk.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by kovtonyk on 29.05.2017.
 */
@Embeddable
@Getter
@Setter
public class DeviceModelHeatRadiatorId implements Serializable {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_model_id")
    private DeviceModel deviceModel;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "heat_radiator_type_id")
    private HeatRadiatorType heatRadiatorType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceModelHeatRadiatorId that = (DeviceModelHeatRadiatorId) o;

        if (deviceModel != null ? !deviceModel.equals(that.deviceModel) : that.deviceModel != null) return false;
        return heatRadiatorType != null ? heatRadiatorType.equals(that.heatRadiatorType) : that.heatRadiatorType == null;
    }

    @Override
    public int hashCode() {
        int result = deviceModel != null ? deviceModel.hashCode() : 0;
        result = 31 * result + (heatRadiatorType != null ? heatRadiatorType.hashCode() : 0);
        return result;
    }


}
