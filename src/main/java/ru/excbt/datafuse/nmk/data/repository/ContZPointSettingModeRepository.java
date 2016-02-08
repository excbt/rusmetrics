package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;

/**
 * Repository для ContZPointSettingMode
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 01.04.2015
 *
 */
public interface ContZPointSettingModeRepository extends CrudRepository<ContZPointSettingMode, Long> {

	public List<ContZPointSettingMode> findByContZPointId(long contZPointId);

	public List<ContZPointSettingMode> findByContZPointIdAndSettingMode(long contZPointId, String settingMode);

}
