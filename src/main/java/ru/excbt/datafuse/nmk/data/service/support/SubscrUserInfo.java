package ru.excbt.datafuse.nmk.data.service.support;

/**
 * Created by kovtonyk on 10.07.2017.
 */
public interface SubscrUserInfo {

    long getSubscriberId();

    long getUserId();

    boolean isRma();

    long getRmaSubscriberId();
}
