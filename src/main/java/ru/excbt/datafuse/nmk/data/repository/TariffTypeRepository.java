package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.TariffType;

public interface TariffTypeRepository extends CrudRepository<TariffType, Long> {

	public List<TariffType> findByContServiceType (String contServiceType);
}
