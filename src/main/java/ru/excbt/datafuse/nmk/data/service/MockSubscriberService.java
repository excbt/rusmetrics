package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;

import java.util.Optional;

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

	private final SubscriberRepository subscriberRepository;

	@Autowired
    public MockSubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

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
	    if (mockSubscriberId == null) {
            logger.error("Mock Subscriber is disabled. Using temporary Subscriber");
            return new Subscriber().id(Long.MIN_VALUE);
        }

		logger.warn("ATTENTION!!! Using MockUser");

		return Optional.ofNullable(subscriberRepository.findOne(mockSubscriberId))
            .orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Subscriber.class, mockSubscriberId));

	}
}
