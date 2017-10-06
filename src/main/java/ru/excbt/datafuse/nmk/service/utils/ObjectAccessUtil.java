package ru.excbt.datafuse.nmk.service.utils;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class ObjectAccessUtil {

    private final ObjectAccessService objectAccessService;

    public ObjectAccessUtil(ObjectAccessService objectAccessService) {
        this.objectAccessService = objectAccessService;
    }

    public Predicate<ContObject> checkContObject(PortalUserIds portalUserIds) {
        final CopyOnWriteArrayList<Long> contObjectIds = new CopyOnWriteArrayList<>(objectAccessService.findContObjectIds(portalUserIds));
        return (co) -> contObjectIds.contains(co.getId());
    }

    public Predicate<ContZPoint> checkContZPoint(PortalUserIds portalUserIds) {
        final CopyOnWriteArrayList<Long> contZPointIds = new CopyOnWriteArrayList<>(objectAccessService.findAllContZPointIds(portalUserIds));
        return (co) -> contZPointIds.contains(co.getId());
    }

}
