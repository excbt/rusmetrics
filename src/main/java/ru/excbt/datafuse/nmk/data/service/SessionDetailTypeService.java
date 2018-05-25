package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.SessionDetailTypeContServiceType;
import ru.excbt.datafuse.nmk.data.model.support.SessionDetailTypeInfo;
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
	@Transactional( readOnly = true)
	public List<SessionDetailTypeInfo> selectByContServiceType(String contServiceType) {
		List<SessionDetailTypeContServiceType> preResult = sessionDetailTypeContServiceTypeRepository
				.selectSessionDetailTypeContServiceType(contServiceType);

		List<SessionDetailTypeInfo> result = preResult.stream().map(i -> new SessionDetailTypeInfo(i)).distinct()
				.collect(Collectors.toList());
		return result;
	}

	/**
	 *
	 * @param contServiceTypes
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<SessionDetailTypeInfo> selectByContServiceType(List<String> contServiceTypes) {
		List<SessionDetailTypeContServiceType> preResult = sessionDetailTypeContServiceTypeRepository
				.selectSessionDetailTypeContServiceType(contServiceTypes);

		List<SessionDetailTypeInfo> result = preResult.stream().map(i -> new SessionDetailTypeInfo(i)).distinct()
				.collect(Collectors.toList());
		return result;
	}

}
