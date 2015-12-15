package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElProfile;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataElTech;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElConsRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElProfileRepository;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataElTechRepository;

@Service
public class ContServiceDataElService {

	@Autowired
	private ContServiceDataElConsRepository contServiceDataElConsRepository;

	@Autowired
	private ContServiceDataElTechRepository contServiceDataElTechRepository;

	@Autowired
	private ContServiceDataElProfileRepository contServiceDataElProfileRepository;

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @param pageRequest
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataElCons> selectConsByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElConsRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo(), pageRequest);

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataElCons> selectConsByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElConsRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo());

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @param pageRequest
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataElTech> selectTechByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElTechRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo(), pageRequest);

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataElTech> selectTechByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElTechRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo());

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param beginDate
	 * @param endDate
	 * @param pageRequest
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<ContServiceDataElProfile> selectProfileByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElProfileRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo(), pageRequest);

	}

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetail
	 * @param localDatePeriod
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataElProfile> selectProfileByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataElProfileRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo());

	}
}
