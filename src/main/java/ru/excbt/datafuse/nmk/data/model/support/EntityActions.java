package ru.excbt.datafuse.nmk.data.model.support;

import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;

import static com.google.common.base.Preconditions.checkNotNull;

public class EntityActions {

    private EntityActions() {
    }

    public static final <T extends DeletableObject> T softDelete(T entity) {
        checkNotNull(entity);
        entity.setDeleted(1);
        return entity;
    }

    public static final <T extends DeletableObject> Iterable<T> softDelete(Iterable<T> entities) {
        checkNotNull(entities);
        entities.forEach(i -> {
            i.setDeleted(1);
        });
        return entities;
    }


}
