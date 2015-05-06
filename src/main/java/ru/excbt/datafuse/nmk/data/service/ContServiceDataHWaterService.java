package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.constant.TimeDetail;
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
	public List<ContServiceDataHWater> selectByContZPoint(long contZPointId,
			TimeDetail timeDetail) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				timeDetail.getKeyname());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<ContServiceDataHWater> selectByContZPoint(long contZPointId,
			TimeDetail timeDetail, DateTime beginDate, DateTime endDate) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				timeDetail.getKeyname(), beginDate.toDate(), endDate.toDate());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public Page<ContServiceDataHWater> selectByContZPoint(long contZPointId,
			TimeDetail timeDetail, DateTime beginDate, DateTime endDate,
			Pageable pageable) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(beginDate, "beginDate is null");
		checkNotNull(endDate, "endDate is null");
		checkArgument(beginDate.compareTo(endDate) <= 0);
		checkNotNull(pageable);

		return contServiceDataHWaterRepository.selectByZPoint(contZPointId,
				timeDetail.getKeyname(), beginDate.toDate(), endDate.toDate(),
				pageable);
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = false)
	public ContServiceDataHWater selectLastData(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository
				.selectLastDetailByZPoint(contZPointId, new PageRequest(0, 1));
		return resultList.size() > 0 ? resultList.get(0) : null;
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = false)
	public Date selectLastDataDate(long contZPointId) {
		checkArgument(contZPointId > 0);
		List<ContServiceDataHWater> resultList = contServiceDataHWaterRepository
				.selectLastDetailByZPoint(contZPointId, new PageRequest(0, 1));
		return resultList.size() > 0 ? resultList.get(0).getDataDate() : null;
	}
}
