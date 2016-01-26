package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;

/**
 * Сервис для работы с типами событий ContEventType
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 30.06.2015
 *
 */

@Service
@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
		List<ContEventType> result = contEventTypeRepository.selectBaseEventTypes(Boolean.TRUE);

		return ObjectFilters.devModeFilter(result.stream()).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param contEventTypeIds
	 * @return
	 */
	public List<Long> selectContEventTypesPaired(List<Long> contEventTypeIds) {
		checkNotNull(contEventTypeIds);
		List<ContEventType> eventTypes = contEventTypeRepository.selectContEventTypes(contEventTypeIds);
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
