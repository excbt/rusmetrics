package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceSubscriberAccess;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceItemService;
import ru.excbt.datafuse.nmk.data.service.SubscrServicePackService;
import ru.excbt.datafuse.nmk.data.service.SubscrServiceSubscriberAccessService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscServiceManageControllerTest extends AnyControllerTest {

	private final static long MANUAL_SUBSCRIBER_ID = 64166467;

	@Autowired
	private SubscrServiceSubscriberAccessService subscrServiceSubscriberAccess;

	@Autowired
	private SubscrServiceItemService subscrServiceItemService;

	@Autowired
	private SubscrServicePackService subscrServicePackService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPackGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/manage/service/servicePackList"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testItemsGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/manage/service/serviceItemList"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPricesGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/manage/service/servicePriceList"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testManualSubscriberAccessGet() throws Exception {
		_testJsonGet(apiSubscrUrl(String.format("/%d/manage/service/access", MANUAL_SUBSCRIBER_ID)));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCurrentSubscriberAccessGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/manage/service/access"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testManualSubscriberUpdateAccess() throws Exception {

		List<SubscrServicePack> packs = subscrServicePackService.findByKeyname("WEB_TEST_SERVICE");
		assertTrue(packs.size() == 1);

		Long testPackId = packs.get(0).getId();

		List<SubscrServiceSubscriberAccess> accessList = subscrServiceSubscriberAccess
				.selectSubscriberServiceAccess(MANUAL_SUBSCRIBER_ID, LocalDate.now());

		accessList.forEach((i) -> {
			i.setId(null);
			i.setSubscriber(null);
			i.setSubscriberId(null);
			i.setAccessStartDate(null);
		});

		Optional<SubscrServiceSubscriberAccess> testServiceCheck = accessList.stream()
				.filter((i) -> testPackId.equals(i.getPackId())).findAny();

		if (testServiceCheck.isPresent()) {
			accessList.remove(testServiceCheck.get());
		} else {
			SubscrServiceSubscriberAccess access = SubscrServiceSubscriberAccess.newInstance(testPackId, null);
			accessList.add(access);
		}

		String url = apiSubscrUrl(String.format("/%d/manage/service/access", MANUAL_SUBSCRIBER_ID));
		_testJsonUpdate(url, accessList, null);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCurrentSubscriberUpdateAccess() throws Exception {

		List<SubscrServicePack> packs = subscrServicePackService.findByKeyname("WEB_TEST_SERVICE");
		assertTrue(packs.size() == 1);

		Long testPackId = packs.get(0).getId();

		List<SubscrServiceSubscriberAccess> accessList = subscrServiceSubscriberAccess
				.selectSubscriberServiceAccess(currentSubscriberService.getSubscriberId(), LocalDate.now());

		accessList.forEach((i) -> {
			i.setId(null);
			i.setSubscriber(null);
			i.setSubscriberId(null);
			i.setAccessStartDate(null);
		});

		Optional<SubscrServiceSubscriberAccess> testServiceCheck = accessList.stream()
				.filter((i) -> testPackId.equals(i.getPackId())).findAny();

		if (testServiceCheck.isPresent()) {
			accessList.remove(testServiceCheck.get());
		} else {
			SubscrServiceSubscriberAccess access = SubscrServiceSubscriberAccess.newInstance(testPackId, null);
			accessList.add(access);
		}

		String url = apiSubscrUrl("/manage/service/access");
		_testJsonUpdate(url, accessList, null);

	}

}
