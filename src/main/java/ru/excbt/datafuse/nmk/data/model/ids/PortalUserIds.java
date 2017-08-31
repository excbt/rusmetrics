package ru.excbt.datafuse.nmk.data.model.ids;

public interface PortalUserIds {

    Long getSubscriberId();

    Long getUserId();

    Long getParentSubscriberId();

    default boolean isRma() {
        return false;
    }

    Long getRmaId();

    default boolean isValid() {
        return getSubscriberId() != null && getUserId() != null;
    }

}
