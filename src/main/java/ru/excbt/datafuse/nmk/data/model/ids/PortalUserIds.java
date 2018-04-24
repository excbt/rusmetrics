package ru.excbt.datafuse.nmk.data.model.ids;

import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;

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

    default boolean haveParentSubacriber() {
        return getParentSubscriberId() != null;
    }

    SubscrTypeKey getSubscrTypeKey();
}
