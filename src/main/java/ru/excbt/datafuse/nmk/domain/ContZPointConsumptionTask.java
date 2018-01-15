package ru.excbt.datafuse.nmk.domain;

import java.time.Instant;
import java.util.UUID;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_consumption_task")
@Getter
@Setter
public class ContZPointConsumptionTask extends AbstractAuditableModel {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "contZPointConsumptionTaskSeq", sequenceName = "portal.cont_zpoint_consumption_task_id_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contZPointConsumptionTaskSeq")
    private Long id;

    @Column(name = "task_uuid")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID taskUUID;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "cons_date_time")
    private Instant consDateTime;

    @Column(name = "task_date_time")
    private Instant taskDateTime;

    @Column(name = "task_state")
    private String taskState;

    @Column(name = "task_state_dt")
    private Instant taskStateDt;

    @Column(name = "cont_zpoint_id")
    private Long contZPointId;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;

}
