package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;

/**
 * Класс "заглушка" для работы с абонентом
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.05.2015
 *
 */
@Service
public class MockSubscriberService {

	private static final Logger logger = LoggerFactory.getLogger(MockSubscriberService.class);

	public static final long DEV_SUBSCR_ORG_ID = 728;

	private Long mockSubscriberId = DEV_SUBSCR_ORG_ID;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @return
	 */
	public Long getMockSubscriberId() {
		return mockSubscriberId;
	}

	/**
	 * 
	 * @param mockSubscriberId
	 */
	public void setMockSubscriberId(Long mockSubscriberId) {
		this.mockSubscriberId = mockSubscriberId;
	}

	/**
	 * 
	 * @return
	 */
	public Subscriber getMockSubscriber() {
		checkState(mockSubscriberId != null, "Mock Subscriber Service is Disabled");

		logger.warn("ATTENTION!!! Using MockUser");
		return subscriberService.selectSubscriber(mockSubscriberId);

	}
}
