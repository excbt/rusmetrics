package ru.excbt.datafuse.nmk.data.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_device_history")
@Getter
@Setter
public class ContZPointDeviceHistory extends AbstractAuditableModel {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cont_zpoint_id", updatable = false)
    private ContZPoint contZPoint;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "device_object_id", updatable = false)
    private DeviceObject deviceObject;

    @Column(name = "start_date", updatable = false)
    @NotNull
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "revision", updatable = false)
    @NotNull
    @Check(constraints = "revision >= 1")
    private Integer revision = 1;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

    @Override
    public String toString() {
        return "ContZPointDeviceHistory{" +
            "contZPoint=" + contZPoint +
            ", deviceObject=" + deviceObject +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", revision=" + revision +
            ", version=" + version +
            ", deleted=" + deleted +
            "} " ;
    }
}
