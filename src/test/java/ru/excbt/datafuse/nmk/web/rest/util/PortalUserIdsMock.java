package ru.excbt.datafuse.nmk.web.rest.util;

import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;

import static org.mockito.Mockito.when;

public class PortalIdsMock {

    public static void initMockService(PortalUserIdsService portalUserIdsService, PortalUserIds portalUserIds) {
        when(portalUserIdsService.getCurrentIds()).thenReturn(portalUserIds);
    }

}
