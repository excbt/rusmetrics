package ru.excbt.datafuse.nmk.data.model.dto;

import java.time.LocalDate;

public class SubscrContZPointStPlanDTO {

    private Long id;

    private Long subscriberId;

    private Long contZPointId;

    private Long subscrStPlanId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String stPlanRole;

    private Boolean isEnabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public Long getContZPointId() {
        return contZPointId;
    }

    public void setContZPointId(Long contZPointId) {
        this.contZPointId = contZPointId;
    }

    public Long getSubscrStPlanId() {
        return subscrStPlanId;
    }

    public void setSubscrStPlanId(Long subscrStPlanId) {
        this.subscrStPlanId = subscrStPlanId;
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
}
