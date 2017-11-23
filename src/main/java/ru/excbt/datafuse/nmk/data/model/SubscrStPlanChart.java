package ru.excbt.datafuse.nmk.data.model;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import javax.persistence.*;


@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "subscr_st_plan_chart")
public class SubscrStPlanChart extends AbstractAuditableModel {

    @ManyToOne
    @JoinColumn(name = "subscr_st_plan_id")
    private SubscrStPlan subscrStPlan;

    @Column(name = "rn")
    private Integer rn;

    @Column(name = "key1")
    private Double key1;

    @Column(name = "key2")
    private Double key2;

    @Column(name = "val1")
    private Double val1;

    @Column(name = "val2")
    private Double val2;

    public SubscrStPlan getSubscrStPlan() {
        return subscrStPlan;
    }

    public void setSubscrStPlan(SubscrStPlan subscrStPlan) {
        this.subscrStPlan = subscrStPlan;
    }

    public Integer getRn() {
        return rn;
    }

    public void setRn(Integer rn) {
        this.rn = rn;
    }

    public Double getKey1() {
        return key1;
    }

    public void setKey1(Double key1) {
        this.key1 = key1;
    }

    public Double getKey2() {
        return key2;
    }

    public void setKey2(Double key2) {
        this.key2 = key2;
    }

    public Double getVal1() {
        return val1;
    }

    public void setVal1(Double val1) {
        this.val1 = val1;
    }

    public Double getVal2() {
        return val2;
    }

    public void setVal2(Double val2) {
        this.val2 = val2;
    }
}
