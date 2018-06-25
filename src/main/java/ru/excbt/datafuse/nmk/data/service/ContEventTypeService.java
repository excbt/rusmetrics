package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Сервис для работы с типами событий ContEventType
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 30.06.2015
 *
 */

@Service
@Transactional( readOnly = true)
public class ContEventTypeService {

	private final ContEventTypeRepository contEventTypeRepository;

    @Autowired
	public ContEventTypeService(ContEventTypeRepository contEventTypeRepository) {
	    this.contEventTypeRepository = contEventTypeRepository;
    }

	/**
	 *
	 * @param contServiceTypeId
	 * @return
	 */
	public ContEventType findOne(Long contServiceTypeId) {
		return contEventTypeRepository.findById(contServiceTypeId).orElseThrow(() -> new EntityNotFoundException(ContEventType.class, contServiceTypeId));
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
