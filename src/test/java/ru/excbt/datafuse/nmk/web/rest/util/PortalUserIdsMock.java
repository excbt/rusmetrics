package ru.excbt.datafuse.nmk.web.rest.util;

import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;

import static org.mockito.Mockito.when;

public class PortalUserIdsMock {

    public static PortalUserIds mockUserIds(Long subscriberId, Long userId) {
        return new PortalUserIds() {
            @Override
            public Long getSubscriberId() {
                return subscriberId;
            }

            @Override
            public Long getUserId() {
                return userId;
            }

            @Override
            public Long getParentSubscriberId() {
                return null;
            }

            @Override
            public Long getRmaId() {
                return null;
            }
        };
    }

    public static PortalUserIds rmaMockUserIds(Long subscriberId, Long userId) {
        return new PortalUserIds() {
            @Override
            public Long getSubscriberId() {
                return subscriberId;
            }

            @Override
            public Long getUserId() {
                return userId;
            }

            @Override
            public Long getParentSubscriberId() {
                return null;
            }

            @Override
            public Long getRmaId() {
                return null;
            }

            @Override
            public boolean isRma() {
                return true;
            }

        };
    }

    public static void initMockService(PortalUserIdsService portalUserIdsService, PortalUserIds portalUserIds) {
        when(portalUserIdsService.getCurrentIds()).thenReturn(portalUserIds);
    }

}
