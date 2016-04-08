package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;

public interface SubscrObjectTreeRepository extends CrudRepository<SubscrObjectTree, Long> {

	@Query("SELECT t FROM SubscrObjectTree t WHERE t.rmaSubscriberId = :rmaSubscriberId")
	List<SubscrObjectTree> selectSubscrObjectTree(@Param("rmaSubscriberId") Long rmaSubscriberId);

}
