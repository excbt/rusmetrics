package ru.excbt.datafuse.nmk.web;

import static com.google.common.base.Preconditions.checkNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigLocal;
import ru.excbt.datafuse.nmk.config.jpa.JpaRawConfigLocal;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;
import ru.excbt.datafuse.nmk.config.mvc.SpringMvcConfig;
import ru.excbt.datafuse.nmk.config.security.LocalSecurityConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= PortalApplication.class)
@WithMockUser(username = "manual-ex1", password = "12345",
    roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
        "RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN" })
@ActiveProfiles(value = { Constants.SPRING_PROFILE_TEST })
public class ManualControllerTest extends AbstractControllerTest {

	private final static long SUBSCR_USER_ID = 64166469; // manual-ex1
	public static final long SUBSCR_ORG_ID = 64166467; // РМА-EXCBT

	@Before
	public void setup() {
		setupAuditor(SUBSCR_USER_ID, SUBSCR_ORG_ID);

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void testInit() {
//		checkNotNull(mockMvc);
	}

}
