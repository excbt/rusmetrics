package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import static com.google.common.base.Preconditions.checkState;

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

	    Subscriber subscriber = subscriberRepository.findById(benchmarkSubscriberId)
            .orElseThrow(() -> new EntityNotFoundException(Subscriber.class, benchmarkSubscriberId));

		this.benchmarkSubscriberId = benchmarkSubscriberId;
	}

	/**
	 *
	 */
	public void reset() {
		benchmarkSubscriberId = null;
	}

}
