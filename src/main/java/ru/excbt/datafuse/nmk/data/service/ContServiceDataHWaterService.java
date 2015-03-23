package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataHWaterRepository;

@Service
@Transactional
public class ContServiceDataHWaterService {

	@Autowired
	private ContServiceDataHWaterRepository contServiceDataHWaterRepository;

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<ContServiceDataHWater> selectByZPoint(final long contZPointId) {
		checkArgument(contZPointId > 0);
		return contServiceDataHWaterRepository.selectByZPoint(contZPointId);
	}

	/**
	 * 
	 * @param contZPointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<ContServiceDataHWater> selectByZPoint(final long contZPointId,
			final DateTime beginDate, final DateTime endDate) {
		checkArgument(contZPointId > 0);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);
		
		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				beginDate.toDate(), endDate.toDate());
	}

}
