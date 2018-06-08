package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.SubscrUserWrapper;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;

@WithMockUser(username = "ex1-cab-admin", password = "12345", roles = { "SUBSCR_USER", "SUBSCR_ADMIN",
		"CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "SUBSCR_CREATE_CHILD", "SUBSCR_CREATE_CABINET" })
@Transactional
public class SubscrCabinetControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetControllerTest.class);

	@Autowired
	private SubscriberService subscriberService;

	/**
	 *
	 * @return
	 */
	@Override
	public long getSubscriberId() {
		return 512156297L;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return 512156325L;
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testContObjectInfoList() throws Exception {
		_testGetJson("/api/subscr/subscrCabinet/contObjectCabinetInfo");
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testCreateCabinetSubscribers() throws Exception {
		List<Long> contObjectIds = Arrays.asList(29863789L, 29863938L, 29863933L);

		_testUpdateJson("/api/subscr/subscrCabinet/create", contObjectIds);
		/**
		 * 29863789
		 * 29863938
		 * 29863933
		 * 29863488
		 * 29863487
		 * 29863944
		 * 66948436
		 * 29863950
		 * 29863957
		 * 29863952
		 * 29863953
		 * 29863949
		 * 29863965
		 * 29863964
		 * 29863934
		 * 29863503
		 * 29863502
		 */

	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testDeleteCabinetSubscriber() throws Exception {

		List<Subscriber> subscribers = subscriberService.selectChildSubscribers(getSubscriberId());

		List<Long> childSubscriberIds = subscribers.stream().map(i -> i.getId()).collect(Collectors.toList());
		_testUpdateJson("/api/subscr/subscrCabinet/delete", childSubscriberIds);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testUpdateSubscrUser() throws Exception {
		String content = _testGetJson("/api/subscr/subscrCabinet/subscrUser/512157249");

		SubscrUserWrapper subscrUserWrapper = TestUtils.fromJSON(new TypeReference<SubscrUserWrapper>() {
		}, content);

		assertNotNull(subscrUserWrapper);

		subscrUserWrapper.getSubscrUser().setDevComment(EDITED_BY_REST + " at " + System.currentTimeMillis());
		subscrUserWrapper.setPasswordPocket("12345");

		_testUpdateJson("/api/subscr/subscrCabinet/subscrUser/512157249", subscrUserWrapper);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testSubscrUserResetPassword() throws Exception {
		List<Long> subscrUserIds = Arrays.asList(512157249L);
		_testUpdateJson("/api/subscr/subscrCabinet/subscrUser/resetPassword", subscrUserIds);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testSubscrUserSendPassword() throws Exception {
		List<Long> subscrUserIds = Arrays.asList(512157254L);
		_testUpdateJson("/api/subscr/subscrCabinet/subscrUser/sendPassword", subscrUserIds);
	}

}
