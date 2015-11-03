package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkState;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.service.SubscriberService;

@Service
public class BenchmarkService {

	private volatile Long benchmarkSubscriberId;

	@Autowired
	private SubscriberService subscriberService;

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

		if (!subscriberService.checkSubscriberId(benchmarkSubscriberId)) {
			throw new PersistenceException(String.format("Subscriber (Id:%d) is not found", benchmarkSubscriberId));
		}
		this.benchmarkSubscriberId = benchmarkSubscriberId;
	}

	/**
	 * 
	 */
	public void reset() {
		benchmarkSubscriberId = null;
	}

}
