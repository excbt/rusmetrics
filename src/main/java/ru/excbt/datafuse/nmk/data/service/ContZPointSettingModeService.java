package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;
import ru.excbt.datafuse.nmk.data.repository.ContZPointSettingModeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class ContZPointSettingModeService implements SecuredRoles {

	@Autowired
	private ContZPointSettingModeRepository contZPointSettingModeRepository;

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContZPointSettingMode> findSettingByContZPointId(
			long contZPointId) {
		return contZPointSettingModeRepository.findByContZPointId(contZPointId);
	}

	/**
	 * 
	 * @param contZPointId
	 * @param settingMode
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContZPointSettingMode> findSettingByContZPointId(
			long contZPointId, String settingMode) {
		return contZPointSettingModeRepository
				.findByContZPointIdAndSettingMode(contZPointId, settingMode);
	}

	/**
	 * 
	 * @param arg
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public ContZPointSettingMode save(ContZPointSettingMode arg) {
		checkNotNull(arg);
		checkNotNull(arg.getContZPoint());

		if (arg.isNew() && arg.getVersion() != 0) {
			throw new PersistenceException(
					"Invalid version field for new record");
		}

		ContZPointSettingMode result = contZPointSettingModeRepository
				.save(arg);

		return result;
	}

	/**
	 * 
	 * @param entity
	 */
	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void delete(ContZPointSettingMode entity) {
		checkNotNull(entity);
		contZPointSettingModeRepository.delete(entity);
	}

	/**
	 * 
	 * @param id
	 */
	@Transactional
	@Secured({ ROLE_ADMIN, SUBSCR_ROLE_ADMIN })
	public void delete(long id) {
		checkArgument(id > 0);
		contZPointSettingModeRepository.delete(id);
	}

}
