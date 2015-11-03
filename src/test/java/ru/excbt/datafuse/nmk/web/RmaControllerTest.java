package ru.excbt.datafuse.nmk.web;

import static com.google.common.base.Preconditions.checkNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.excbt.datafuse.nmk.config.jpa.JpaConfigLocal;
import ru.excbt.datafuse.nmk.config.jpa.JpaRawConfigLocal;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;
import ru.excbt.datafuse.nmk.config.mvc.SpringMvcConfig;
import ru.excbt.datafuse.nmk.config.security.LocalSecurityConfig;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { SpringMvcConfig.class, JpaConfigLocal.class, JpaRawConfigLocal.class,
		LocalSecurityConfig.class, LdapConfig.class })
@WithMockUser(username = "rma-ex1", password = "12345",
		roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
				"RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN" })
public class RmaControllerTest extends AbstractControllerTest implements TestExcbtRmaIds {

	@Before
	public void setup() {
		setupAuditor(RMA_USER_ID, RMA_SUBSCRIBER_ID);

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilters(springSecurityFilterChain).build();
	}

	@Test
	public void testInit() {
		checkNotNull(mockMvc);
	}

}
