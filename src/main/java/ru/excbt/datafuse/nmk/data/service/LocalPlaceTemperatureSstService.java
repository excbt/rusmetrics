package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.LocalPlaceTemperatureSst;
import ru.excbt.datafuse.nmk.data.repository.LocalPlaceTemperatureSstRepository;

@Service
public class LocalPlaceTemperatureSstService {

	private static final Logger logger = LoggerFactory.getLogger(LocalPlaceTemperatureSstService.class);

	@Autowired
	private LocalPlaceTemperatureSstRepository localPlaceTemperatureSstRepository;

	/**
	 * 
	 * @param localPlaceId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<LocalPlaceTemperatureSst> selectByLocalPlace(Long localPlaceId, LocalDate sstDate) {
		checkNotNull(localPlaceId);
		checkNotNull(sstDate);
		LocalDate beginDate = sstDate.withDayOfMonth(1);
		LocalDate endDate = beginDate.plusMonths(1).minusDays(1);
		return localPlaceTemperatureSstRepository.selectByLocalPlace(localPlaceId, beginDate.toDate(),
				endDate.toDate());
	}

}
