package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkState;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;

import java.util.Optional;

@Service
public class BenchmarkService {

	private volatile Long benchmarkSubscriberId;

	private final SubscriberRepository subscriberRepository;

	@Autowired
    public BenchmarkService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    /**
	 *
	 * @return
	 */
	public Long getBenchmarkSubscriberId() {
		checkState(benchmarkSubscriberId != null);
		return benchmarkSubscriberId;
	}

	/**
	 *
	 * @param benchmarkSubscriberId
	 */
	public void setBenchmarkSubscriberId(Long benchmarkSubscriberId) {

        Optional.ofNullable(subscriberRepository.findOne(benchmarkSubscriberId))
            .orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Subscriber.class, benchmarkSubscriberId));

		this.benchmarkSubscriberId = benchmarkSubscriberId;
	}

	/**
	 *
	 */
	public void reset() {
		benchmarkSubscriberId = null;
	}

}
