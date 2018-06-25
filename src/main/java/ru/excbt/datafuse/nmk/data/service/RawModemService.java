package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.RawModemModel;
import ru.excbt.datafuse.nmk.data.repository.RawModemModelRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

@Service
public class RawModemService implements SecuredRoles {

	@Autowired
	private RawModemModelRepository rawModemModelRepository;

	/**
	 *
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<RawModemModel> selectRawModels() {
		return ObjectFilters.deletedFilter(rawModemModelRepository.selectRawModels());
	}

	/**
	 *
	 * @param rawModemModelId
	 * @return
	 */
	@Transactional( readOnly = true)
	public RawModemModel selectRawModel(Long rawModemModelId) {
		return rawModemModelRepository.findById(rawModemModelId)
            .orElseThrow(() -> new EntityNotFoundException(RawModemModel.class, rawModemModelId));
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_ADMIN })
	@Transactional
	public RawModemModel saveRawModemModel(RawModemModel entity) {
		return rawModemModelRepository.save(entity);
	}

	/**
	 *
	 * @param rawModemModelId
	 */
	@Secured({ ROLE_ADMIN })
	@Transactional( readOnly = true)
	public void deleteRawModemModel(Long rawModemModelId) {
		RawModemModel deleteCadidate = rawModemModelRepository.findById(rawModemModelId)
            .orElseThrow(() -> new EntityNotFoundException(RawModemModel.class, rawModemModelId));

		if (Boolean.TRUE.equals(deleteCadidate.getIsProtected())) {
			throw new AccessDeniedException(String.format("RawModemModel (id=%s) can't be deleted", rawModemModelId));
		}
		rawModemModelRepository.delete(deleteCadidate);
	}

}
