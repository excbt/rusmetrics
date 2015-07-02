package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

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

	/**
	 * 
	 * @return
	 */
	public List<ContEventType> selectBaseContEventTypes() {
		List<ContEventType> result = contEventTypeRepository
				.selectBaseEventTypes(Boolean.TRUE);
		return result;
	}

	/**
	 * 
	 * @param contEventTypeIds
	 * @return
	 */
	public List<Long> selectContEventTypesPaired(List<Long> contEventTypeIds) {
		checkNotNull(contEventTypeIds);
		List<ContEventType> eventTypes = contEventTypeRepository
				.selectContEventTypes(contEventTypeIds);
		List<Long> result = new ArrayList<>();
		for (ContEventType item : eventTypes) {
			result.add(item.getId());
			if (item.getReverseId() != null) {
				result.add(item.getReverseId());
			}
		}
		return result;
	}

}
