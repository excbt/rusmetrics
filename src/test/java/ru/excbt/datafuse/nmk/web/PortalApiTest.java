package ru.excbt.datafuse.nmk.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;

@SpringBootTest(classes = PortalApplicationTest.class)
@WithMockUser(username = "admin", password = "admin",
    roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
        "RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN", "SUBSCR_CREATE_CABINET",
        "CABINET_USER" })
public class PortalApiTest {

}
