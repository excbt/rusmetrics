package ru.excbt.datafuse.nmk.data.model;


import org.hibernate.annotations.Check;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_device_history")
public class ContZPointDeviceHistory extends AbstractAuditableModel {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cont_zpoint_id", updatable = false)
    @NotNull
    private ContZPoint contZPoint;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_object_id", updatable = false)
    @NotNull
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

    public ContZPoint getContZPoint() {
        return contZPoint;
    }

    public void setContZPoint(ContZPoint contZPoint) {
        this.contZPoint = contZPoint;
    }

    public DeviceObject getDeviceObject() {
        return deviceObject;
    }

    public void setDeviceObject(DeviceObject deviceObject) {
        this.deviceObject = deviceObject;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }


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
