package ru.excbt.datafuse.nmk.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.app.PortalApplicationTest;
import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.model.support.SubscriberUserInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= PortalApplicationTest.class)
@WithMockUser(username = "admin", password = "admin",
		roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
				"RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN", "SUBSCR_CREATE_CABINET",
				"CABINET_USER" })
public abstract class AnyControllerTest extends AbstractControllerTest implements SubscriberUserInfo {

	private final static long TEST_AUDIT_USER = 1;
	public static final long DEV_SUBSCR_ORG_ID = 728;

	public final static String EDITED_BY_REST = "Edited By REST";

	public final static String API_RMA = "/api/rma";
	public final static String API_SUBSCR = "/api/subscr";

	/**
	 *
	 */
	public void setupAuditor() {
		setupAuditor(getSubscrUserId(), getSubscriberId());
	}

	/**
	 *
	 */
	@Before
	public void setup() {
		setupAuditor();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilters(springSecurityFilterChain).build();
	}

	/**
	 *
	 */
	@Test
	public void testInit() {
		//checkNotNull(mockMvc);
	}

	/**
	 *
	 */

	@Override
	public long getSubscriberId() {
		return DEV_SUBSCR_ORG_ID;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return TEST_AUDIT_USER;
	}

}
