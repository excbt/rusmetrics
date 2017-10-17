package ru.excbt.datafuse.nmk.data.model.support;

/**
 * Created by kovtonyk on 07.07.2017.
 */
public interface ContZPointShortInfo extends ContZPointIdPair {
    Long getContZPointId();

    Long getContObjectId();

    String getCustomServiceName();

    String getContServiceType();

    String getContServiceTypeCaption();
}
