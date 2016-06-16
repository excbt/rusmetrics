package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.LogSession;
import ru.excbt.datafuse.nmk.data.model.SubscrSessionTask;
import ru.excbt.datafuse.nmk.data.repository.SubscrSessionTaskLogRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrSessionTaskRepository;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;

@Service
public class SubscrSessionTaskService {

	@Autowired
	private SubscrSessionTaskRepository subscrSessionTaskRepository;

	@Autowired
	private SubscrSessionTaskLogRepository subscrSessionTaskLogRepository;

	/**
	 * 
	 * @param task
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrSessionTask saveSubscrSessionTask(SubscrSessionTask task) {
		SubscrSessionTask result = subscrSessionTaskRepository.save(task);
		return result;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrSessionTask findSubscrSessionTask(Long id) {
		return subscrSessionTaskRepository.findOne(id);
	}

	/**
	 * 
	 * @param subscrSessionTaskId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean getIsProgress(Long subscrSessionTaskId) {
		SubscrSessionTask task = subscrSessionTaskRepository.findOne(subscrSessionTaskId);
		return Boolean.TRUE.equals(task.getTaskIsComplete());
	}

	/**
	 * 
	 * @param task
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrSessionTask createSubscrSessionTask(SubscrSessionTask task) {

		checkArgument(checkTaskValid(task));

		SubscrSessionTask result = subscrSessionTaskRepository.save(task);
		result.setTaskCreateDate(new Date());
		return result;
	}

	/**
	 * 
	 * @param task
	 * @return
	 */
	public boolean checkTaskValid(SubscrSessionTask task) {
		return task != null && task.getSubscriberId() != null && task.getSubscrUserId() != null
		//&& task.getContZpointId() != null 
				&& task.getDeviceObjectId() != null;
	}

	/**
	 * 
	 * @param subscriberParam
	 * @param task
	 */
	public void initTask(SubscriberParam subscriberParam, SubscrSessionTask task) {
		checkNotNull(subscriberParam);
		checkNotNull(task);
		task.setSubscriberId(subscriberParam.getSubscriberId());
		task.setSubscrUserId(subscriberParam.getSubscrUserId());
	}

	/**
	 * 
	 * @param subscrSessionTaskId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LogSession> selectTaskLogSessions(Long subscrSessionTaskId) {
		return subscrSessionTaskLogRepository
				.selectTaskLogSessions(subscrSessionTaskId != null ? subscrSessionTaskId : 0);
	}

}
