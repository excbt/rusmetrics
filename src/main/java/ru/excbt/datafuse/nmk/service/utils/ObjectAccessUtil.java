package ru.excbt.datafuse.nmk.service.utils;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;

import java.util.ArrayList;
import java.util.Collection;
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

    public Predicate<Long> checkContObjectId(PortalUserIds portalUserIds) {
        final List<Long> contObjectIds = objectAccessService.findContObjectIds(portalUserIds);
        return (i) -> contObjectIds.contains(i);
    }

    public Predicate<ContZPoint> checkContZPoint(PortalUserIds portalUserIds) {
        final CopyOnWriteArrayList<Long> contZPointIds = new CopyOnWriteArrayList<>(objectAccessService.findAllContZPointIds(portalUserIds));
        return (co) -> contZPointIds.contains(co.getId());
    }

    public Predicate<Long> checkContZPointId(PortalUserIds portalUserIds) {
        final List<Long> contZPointIds = objectAccessService.findAllContZPointIds(portalUserIds);
        return (i) -> contZPointIds.contains(i);
    }

    public static final boolean checkIds(Collection<Long> checkIds, Collection<Long> availableIds) {

        if (availableIds == null || availableIds.isEmpty()) {
            return false;
        }

        boolean result = true;
        for (Long id : checkIds) {
            result = result && availableIds.contains(id);
        }
        return result;
    }

}
