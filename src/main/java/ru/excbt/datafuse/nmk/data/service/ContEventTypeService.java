package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;

@Service
@Transactional(readOnly = true)
public class ContEventTypeService {

	@Autowired
	private ContEventTypeRepository contEventTypeRepository;

	/**
	 * 
	 * @param contServiceTypeId
	 * @return
	 */
	public ContEventType findOne(Long contServiceTypeId) {
		return contEventTypeRepository.findOne(contServiceTypeId);
	}
}
