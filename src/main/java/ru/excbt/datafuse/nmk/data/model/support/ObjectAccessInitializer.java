package ru.excbt.datafuse.nmk.data.model.support;

import java.time.LocalDate;

public interface ObjectAccessInitializer {
    Long getId();
    void objectAccess(ObjectAccess.AccessType accessType, LocalDate date);
}
