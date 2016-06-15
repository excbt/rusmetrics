package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.keyname.SessionDetailType;
import ru.excbt.datafuse.nmk.data.repository.SessionDetailTypeContServiceTypeRepository;

@Service
public class SessionDetailTypeService {

	@Autowired
	private SessionDetailTypeContServiceTypeRepository sessionDetailTypeContServiceTypeRepository;

	/**
	 * 
	 * @param contServiceType
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SessionDetailType> selectByContServiceType(String contServiceType) {
		List<SessionDetailType> result = sessionDetailTypeContServiceTypeRepository
				.selectSessionDetailType(contServiceType);
		return ObjectFilters.deletedFilter(result);
	}

}
