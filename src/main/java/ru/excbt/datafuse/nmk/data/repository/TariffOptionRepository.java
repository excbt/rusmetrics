package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.TariffOption;

public interface TariffOptionRepository extends CrudRepository<TariffOption, String> {

}
