package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrSmsLog;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.repository.SubscrSmsLogRepository;

@Service
public class SubscrSmsLogService {

	@Autowired
	private SubscrSmsLogRepository subscrSmsLogRepository;

	/**
	 * 
	 * @param subscriberId
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrSmsLog> selectSmsLog(Long subscriberId, LocalDatePeriod localDatePeriod) {
		return subscrSmsLogRepository.selectBySubscriber(subscriberId, localDatePeriod.getDateFrom(),
				localDatePeriod.getDateTo());
	}

}
