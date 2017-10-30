package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;
import ru.excbt.datafuse.nmk.data.model.keyname.ContObjectSettingModeType;
import ru.excbt.datafuse.nmk.data.repository.ContZPointSettingModeRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

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

	@Autowired
	private ContZPointSettingModeRepository settingModeRepository;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private ContZPointService contZPointService;

	/**
	 *
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
	@Transactional(value = TxConst.TX_DEFAULT)
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
	//@Transactional(value = TxConst.TX_DEFAULT)
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
	//@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN })
	@Deprecated
	private void delete(long id) {
		checkArgument(id > 0);
		settingModeRepository.delete(id);
	}

	/**
	 *
	 * @param contZPointId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ZPOINT_ADMIN })
	public void initContZPointSettingMode(long contZPointId) {

		ContZPoint contZPoint = contZPointService.findOne(contZPointId);

		if (contZPoint == null) {
			throw new PersistenceException(String.format("ContZPoint(id:{}) not found", contZPointId));
		}

		List<ContObjectSettingModeType> settingModeCheckList = contObjectService.selectContObjectSettingModeType();

		for (ContObjectSettingModeType check : settingModeCheckList) {
			List<ContZPointSettingMode> mode = settingModeRepository.findByContZPointIdAndSettingMode(contZPointId,
					check.getKeyname());
			if (mode.size() == 0) {
				ContZPointSettingMode newMode = new ContZPointSettingMode();
				newMode.setContZPoint(contZPoint);
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public ContZPointSettingMode findOne(long contZPointSettingModeId) {
		return settingModeRepository.findOne(contZPointSettingModeId);
	}

	/**
	 *
	 * @param contZPointId
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteByContZPoint(Long contZPointId) {
		checkNotNull(contZPointId);
		List<ContZPointSettingMode> deleteCandidate = findSettingByContZPointId(contZPointId);
		settingModeRepository.delete(deleteCandidate);
	}
}
