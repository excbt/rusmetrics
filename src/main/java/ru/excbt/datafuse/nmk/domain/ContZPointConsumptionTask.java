package ru.excbt.datafuse.nmk.domain;

import java.util.UUID;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_consumption_task")
@Getter
@Setter
public class ContZPointConsumptionTask extends AbstractAuditableModel {

    @Column(name = "task_uuid")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID taskUUID;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "task_date_time")
    private LocalDateTime taskDateTime;

    @Column(name = "task_state")
    private String taskState;

    @Column(name = "task_state_dt")
    private LocalDateTime taskStateDt;

    @Column(name = "cont_zpoint_id")
    private Long contZPointId;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

}
