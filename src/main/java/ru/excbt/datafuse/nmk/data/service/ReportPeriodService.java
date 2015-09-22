package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportPeriod;
import ru.excbt.datafuse.nmk.data.repository.keyname.ReportPeriodRepository;
import ru.excbt.datafuse.nmk.report.ReportPeriodKey;

@Service
public class ReportPeriodService {

	@Autowired
	private ReportPeriodRepository reportPeriodRepository;

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)	
	public ReportPeriod findByKeyname(String keyname) {
		List<ReportPeriod> resultList = reportPeriodRepository
				.findByKeynameIgnoreCase(keyname);
		return resultList.size() == 1 ? resultList.get(0) : null;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ReportPeriod findByKeyname(ReportPeriodKey key) {
		List<ReportPeriod> resultList = reportPeriodRepository
				.findByKeynameIgnoreCase(key.name());
		return resultList.size() == 1 ? resultList.get(0) : null;
	}
}
