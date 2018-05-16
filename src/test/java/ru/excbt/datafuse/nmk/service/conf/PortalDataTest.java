package ru.excbt.datafuse.nmk.service.conf;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest(classes = PortalDataTestConfiguration.class)
@WithMockUser(username = "admin", password = "admin",
    roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
        "RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN", "SUBSCR_CREATE_CABINET",
        "CABINET_USER" })
@Transactional
public abstract class PortalDataTest {

}
