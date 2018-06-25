package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.repository.ContZPointSettingModeRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContObjectSettingModeTypeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

/**
 * Сервис для работы с настройками точки учета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 01.04.2015
 *
 */
@Service
public class ContZPointSettingModeService implements SecuredRoles {

	private final static boolean ZPOINT_SETTING_AUTO_INIT = true;
	private final static int ZPOINT_SETTING_AUTO_INIT_CNT = 2;

	private final ContZPointSettingModeRepository settingModeRepository;

    private final ContObjectSettingModeTypeRepository contObjectSettingModeTypeRepository;

    @Autowired
    public ContZPointSettingModeService(ContZPointSettingModeRepository settingModeRepository, ContObjectSettingModeTypeRepository contObjectSettingModeTypeRepository) {
        this.settingModeRepository = settingModeRepository;
        this.contObjectSettingModeTypeRepository = contObjectSettingModeTypeRepository;
    }

    /**
	 *
	 * @param contZPointId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContZPointSettingMode> findSettingByContZPointId(long contZPointId) {

		List<ContZPointSettingMode> result = settingModeRepository.findByContZPointId(contZPointId);

		// // Auto insert ZPointSettingMode record if option enabled
		// if (result.size() < ZPOINT_SETTING_AUTO_INIT_CNT
		// && ZPOINT_SETTING_AUTO_INIT) {
		// initContZPointSettingMode(contZPointId);
		// result = settingModeRepository.findByContZPointId(contZPointId);
		// }

		return result;
	}

	/**
	 *
	 * @param contZPointId
	 * @param settingMode
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<ContZPointSettingMode> findSettingByContZPointId(long contZPointId, String settingMode) {

		List<ContZPointSettingMode> result = settingModeRepository.findByContZPointIdAndSettingMode(contZPointId,
				settingMode);

		// // Auto insert ZPointSettingMode record if option enabled
		// if (result.size() == 0 && ZPOINT_SETTING_AUTO_INIT) {
		// initContZPointSettingMode(contZPointId);
		// result = settingModeRepository.findByContZPointIdAndSettingMode(
		// contZPointId, settingMode);
		// }

		return result;
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_ZPOINT_ADMIN })
	public ContZPointSettingMode save(ContZPointSettingMode arg) {
		checkNotNull(arg);
		checkNotNull(arg.getContZPoint().getId());

		if (arg.isNew()) {
			throw new PersistenceException("Creating new record of ZPointSettingMode is not allowed");
		}

		ContZPointSettingMode result = settingModeRepository.save(arg);

		return result;
	}

	/**
	 *
	 * @param entity
	 */
	//@Transactional
	@Secured({ ROLE_ZPOINT_ADMIN })
	@Deprecated
	private void delete(ContZPointSettingMode entity) {
		checkNotNull(entity);
		settingModeRepository.delete(entity);
	}

	/**
	 *
	 * @param id
	 */
	//@Transactional
	@Secured({ ROLE_ZPOINT_ADMIN })
	@Deprecated
	private void delete(long id) {
		checkArgument(id > 0);
		settingModeRepository.deleteById(id);
	}

	/**
	 *
	 * @param contZPointId
	 */
	@Transactional
	@Secured({ ROLE_ZPOINT_ADMIN })
	public void initContZPointSettingMode(long contZPointId) {

//		ContZPoint contZPoint = contZPointService.findOne(contZPointId);
//
//		if (contZPoint == null) {
//			throw new PersistenceException(String.format("ContZPoint(id:{}) not found", contZPointId));
//		}

		List<ContObjectSettingModeType> settingModeCheckList = contObjectSettingModeTypeRepository.findAll();

		for (ContObjectSettingModeType check : settingModeCheckList) {
			List<ContZPointSettingMode> mode = settingModeRepository.findByContZPointIdAndSettingMode(contZPointId,
					check.getKeyname());
			if (mode.size() == 0) {
				ContZPointSettingMode newMode = new ContZPointSettingMode();
				newMode.setContZPoint(new ContZPoint().id(contZPointId));
				newMode.setSettingMode(check.getKeyname());
				settingModeRepository.save(newMode);
			}
		}
	}

	/**
	 *
	 * @param contZPointSettingModeId
	 * @return
	 */
	@Transactional( readOnly = true)
	public ContZPointSettingMode findOne(long contZPointSettingModeId) {
		return settingModeRepository.findById(contZPointSettingModeId)
            .orElseThrow(() -> new EntityNotFoundException(ContZPointSettingMode.class, contZPointSettingModeId));
	}

	/**
	 *
	 * @param contZPointId
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	@Transactional
	public void deleteByContZPoint(Long contZPointId) {
		checkNotNull(contZPointId);
		List<ContZPointSettingMode> deleteCandidate = findSettingByContZPointId(contZPointId);
		settingModeRepository.deleteAll(deleteCandidate);
	}
}
