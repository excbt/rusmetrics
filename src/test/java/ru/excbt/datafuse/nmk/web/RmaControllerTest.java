package ru.excbt.datafuse.nmk.web;

import org.springframework.security.test.context.support.WithMockUser;

import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

@WithMockUser(username = "rma-ex1", password = "12345",
		roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
				"RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN", })
public class RmaControllerTest extends AnyControllerTest implements TestExcbtRmaIds {

	/**
	 *
	 * @return
	 */
	@Override
	public long getSubscriberId() {
		return EXCBT_RMA_SUBSCRIBER_ID;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return EXCBT_RMA_SUBSCRIBER_USER_ID;
	}

}
