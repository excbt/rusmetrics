package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;

/**
 * Repository для DeviceObjectMetaVzlet
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.05.2015
 *
 */
public interface DeviceObjectMetaVzletRepository extends CrudRepository<DeviceObjectMetaVzlet, Long> {

	public List<DeviceObjectMetaVzlet> findByDeviceObjectId(Long deviceObjectId);

}