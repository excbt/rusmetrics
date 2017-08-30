package ru.excbt.datafuse.nmk.data.model.ids;

public interface CabinetUserIds extends PortalUserIds {

    Long getParentSubscriberId();

    @Override
    default Long getRmaId() {
        return null;
    }
}
