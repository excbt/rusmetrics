package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.domain.SubscrTypePref;

public interface SubscrTypePrefRepository extends CrudRepository<SubscrTypePref, Long> {

	public List<SubscrTypePref> findBySubscrType(String subscrType);
}
