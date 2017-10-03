package ru.excbt.datafuse.nmk.data.model.ids;

public interface PortalRmaIds extends PortalUserIds {

    @Override
    default boolean isRma() {
        return true;
    }

    @Override
    default Long getRmaId() {
        return getSubscriberId();
    }

}
