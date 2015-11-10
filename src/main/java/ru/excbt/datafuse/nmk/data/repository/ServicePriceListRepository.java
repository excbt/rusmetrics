package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ServicePriceList;

public interface ServicePriceListRepository extends CrudRepository<ServicePriceList, Long> {

	@Query("SELECT pl FROM ServicePriceList pl WHERE pl.priceListLevel = :priceListLevel")
	public List<ServicePriceList> findByLevel(@Param("priceListLevel") Integer priceListLevel);

}
