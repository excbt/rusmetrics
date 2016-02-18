package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.TariffType;

/**
 * Repository для TariffType
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.04.2015
 *
 */
public interface TariffTypeRepository extends CrudRepository<TariffType, Long> {

	public List<TariffType> findByContServiceType(String contServiceType);
}
