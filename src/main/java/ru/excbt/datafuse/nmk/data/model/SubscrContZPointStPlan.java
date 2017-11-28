package ru.excbt.datafuse.nmk.data.model;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_cont_zpoint_st_plan")
public class SubscrContZPointStPlan extends AbstractAuditableModel implements DeletedMarker {

    @Column(name = "subscriber_id")
    private Long subscriberId;

    @ManyToOne
    @JoinColumn(name = "cont_zpoint_id")
    private ContZPoint contZPoint;

    @ManyToOne
    @JoinColumn(name = "subscr_st_plan_id")
    private SubscrStPlan subscrStPlan;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "st_plan_role")
    private String stPlanRole;

    @Column(name = "is_enabled")
    private Boolean isEnabled = false;

    @Column(name = "deleted")
    private int deleted;

    @Version
    private int version;

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public ContZPoint getContZPoint() {
        return contZPoint;
    }

    public void setContZPoint(ContZPoint contZPoint) {
        this.contZPoint = contZPoint;
    }

    public SubscrStPlan getSubscrStPlan() {
        return subscrStPlan;
    }

    public void setSubscrStPlan(SubscrStPlan subscrStPlan) {
        this.subscrStPlan = subscrStPlan;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStPlanRole() {
        return stPlanRole;
    }

    public void setStPlanRole(String stPlanRole) {
        this.stPlanRole = stPlanRole;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "SubscrStPlanZPoint{" +
            "subscriberId=" + subscriberId +
            ", contZPoint=" + contZPoint +
            ", subscrStPlan=" + subscrStPlan +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", stPlanRole='" + stPlanRole + '\'' +
            ", isEnabled=" + isEnabled +
            "} " + super.toString();
    }
}
