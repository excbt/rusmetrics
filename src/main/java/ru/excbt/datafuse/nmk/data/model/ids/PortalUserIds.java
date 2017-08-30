package ru.excbt.datafuse.nmk.data.model.ids;

public interface PortalUserIds {

    Long getSubscriberId();

    Long getUserId();

    default boolean isRma() {
        return false;
    }

    Long getRmaSubscriberId();

    default boolean isValid() {
        return getRmaSubscriberId() != null && getUserId() != null;
    }

}
