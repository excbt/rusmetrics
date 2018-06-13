package ru.excbt.datafuse.nmk.repository.support;

public interface SubscrDeviceObjectNumber {
    Long getSubscriberId();
    Long getContObjectId();
    Long getContZPointId();
    Integer getTsNumber();
    Boolean getIsManualLoading();
    Long getDeviceObjectId();
    String getDeviceObjectNumber();
    Long getSubscrDataSourceId();

}
