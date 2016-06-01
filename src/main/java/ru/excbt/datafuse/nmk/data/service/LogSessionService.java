package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.LogSession;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.repository.LogSessionRepository;
import ru.excbt.datafuse.nmk.data.repository.LogSessionStepRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;

@Service
public class LogSessionService extends AbstractService {

	@Autowired
	private LogSessionRepository logSessionRepository;

	@Autowired
	private LogSessionStepRepository logSessionStepRepository;

	/**
	 * 
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LogSession> selectLogSessions(LocalDatePeriod localDatePeriod) {
		return logSessionRepository.selectLogSessions(localDatePeriod.getDateFrom(), localDatePeriod.getDateTo());
	}

	/**
	 * 
	 * @param localDatePeriod
	 * @param contObjectIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LogSession> selectLogSessions(LocalDatePeriod localDatePeriod, List<Long> contObjectIds) {
		return logSessionRepository.selectLogSessions(localDatePeriod.getDateFrom(), localDatePeriod.getDateTo(),
				contObjectIds);
	}

}
