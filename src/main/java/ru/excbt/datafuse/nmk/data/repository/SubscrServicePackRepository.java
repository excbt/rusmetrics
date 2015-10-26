package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;

public interface SubscrServicePackRepository extends JpaRepository<SubscrServicePack, Long> {

	public List<SubscrServicePack> findByKeyname(String keyname);

	public List<SubscrServicePack> findByIsPersistentService(Boolean value);
}
