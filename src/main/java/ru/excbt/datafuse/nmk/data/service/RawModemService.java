package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.RawModemModel;
import ru.excbt.datafuse.nmk.data.repository.RawModemModelRepository;

@Service
public class RawModemService {

	@Autowired
	private RawModemModelRepository rawModemModelRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<RawModemModel> selectRawModels() {
		return ObjectFilters.deletedFilter(rawModemModelRepository.selectRawModels());
	}

}
