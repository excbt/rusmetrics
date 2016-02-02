package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;

/**
 * Repository для SubscrServicePack
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.09.2015
 *
 */
public interface SubscrServicePackRepository extends JpaRepository<SubscrServicePack, Long> {

	public List<SubscrServicePack> findByKeyname(String keyname);

	public List<SubscrServicePack> findByIsPersistentService(Boolean value);
}
