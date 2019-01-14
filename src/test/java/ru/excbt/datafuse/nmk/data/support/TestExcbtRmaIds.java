package ru.excbt.datafuse.nmk.data.support;

import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

public final class TestExcbtRmaIds {
	public final static long EXCBT_RMA_SUBSCRIBER_USER_ID = 64166468; // rma-ex1
	public static final long EXCBT_RMA_SUBSCRIBER_ID = 64166466; // РМА-EXCBT
	public static final long EXCBT_SUBSCRIBER_ID = 64166467; // РМА-EXCBT
	public static final long EXCBT_ORGANIZATION_ID = 726;
	public static final long EXCBT_SUBSCR_ID = 64166468; // РМА-EXCBT

    public static final PortalUserIds ExcbtRmaPortalUserIds = PortalUserIdsMock.rmaMockUserIds(
        EXCBT_RMA_SUBSCRIBER_ID,
        EXCBT_RMA_SUBSCRIBER_USER_ID);

}
