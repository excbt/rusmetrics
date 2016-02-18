package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceModel;

/**
 * Repository для DeviceModel
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.02.2015
 *
 */
public interface DeviceModelRepository extends CrudRepository<DeviceModel, Long> {

	public List<DeviceModel> findByExSystem(String exSystem);

}
