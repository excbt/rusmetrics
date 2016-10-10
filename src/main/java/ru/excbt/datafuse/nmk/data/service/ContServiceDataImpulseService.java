/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataImpulse;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.repository.ContServiceDataImpulseRepository;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 * 
 */
@Service
public class ContServiceDataImpulseService {

	@Autowired
	private ContServiceDataImpulseRepository contServiceDataImpulseRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContServiceDataImpulse> findAll() {
		return Lists.newArrayList(contServiceDataImpulseRepository.findAll());
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
	public Page<ContServiceDataImpulse> selectImpulseByContZPoint(Long contZPointId, TimeDetailKey timeDetail,
			LocalDatePeriod localDatePeriod, PageRequest pageRequest) {
		checkArgument(contZPointId > 0);
		checkNotNull(timeDetail);
		checkNotNull(localDatePeriod);
		checkArgument(localDatePeriod.isValidEq(), "LocalDatePeriod is invalid");

		return contServiceDataImpulseRepository.selectByZPoint(contZPointId, timeDetail.getKeyname(),
				localDatePeriod.getDateFrom(), localDatePeriod.getDateTo(), pageRequest);

	}

}
