package ru.excbt.datafuse.nmk.utils;

import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

import static org.mockito.Mockito.when;

public final class ExcbtSubscriberMock {

    private ExcbtSubscriberMock() {
    }

    public static void setupSubscriber(PortalUserIds portalUserIds) {
        when(portalUserIds.getSubscriberId()).thenReturn(TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID);
        when(portalUserIds.getUserId()).thenReturn(TestExcbtRmaIds.EXCBT_SUBSCR_ID);
    }

    public static void setupRma(PortalUserIds portalUserIds) {
        when(portalUserIds.getSubscriberId()).thenReturn(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);
        when(portalUserIds.getUserId()).thenReturn(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID);
    }

    public static void setupNulls(PortalUserIds portalUserIds) {
        when(portalUserIds.getSubscriberId()).thenReturn(null);
        when(portalUserIds.getUserId()).thenReturn(null);
    }

}
