package ru.excbt.datafuse.nmk.data.model;


import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by kovtonyk on 27.06.2017.
 */

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_zpoint_access_history")
@Getter
@Setter
public class ContZPointAccessHistory extends AbstractAuditableModel {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cont_zpoint_id")
    private ContZPoint contZPoint;

    @NotNull
    @Column(name = "grant_date", updatable = false)
    private LocalDate grantDate;

    @NotNull
    @Column(name = "grant_time", updatable = false)
    private LocalTime grantTime;

    @Column(name = "revoke_date")
    private LocalDate revokeDate;

    @Column(name = "revoke_time")
    private LocalTime revokeTime;

    @Version
    private int version;

    @Column(name = "deleted")
    private int deleted;
}
