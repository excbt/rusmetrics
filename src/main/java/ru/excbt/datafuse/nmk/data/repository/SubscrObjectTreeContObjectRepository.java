package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeContObject;

public interface SubscrObjectTreeContObjectRepository extends JpaRepository<SubscrObjectTreeContObject, Long> {

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Query("SELECT co FROM ContObject co WHERE co.id IN " + " (SELECT t.contObjectId FROM SubscrObjectTreeContObject t "
			+ " WHERE t.subscrObjectTreeId = :subscrObjectTreeId) " + " ORDER BY co.fullAddress, co.id")
	public List<ContObject> selectContObjects(@Param("subscrObjectTreeId") Long subscrObjectTreeId);

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Query("SELECT t.contObjectId FROM SubscrObjectTreeContObject t "
			+ " WHERE t.subscrObjectTreeId = :subscrObjectTreeId")
	public List<Long> selectContObjectIds(@Param("subscrObjectTreeId") Long subscrObjectTreeId);

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Query("SELECT t FROM SubscrObjectTreeContObject t WHERE t.subscrObjectTreeId = :subscrObjectTreeId")
	public List<SubscrObjectTreeContObject> selectSubscrObjectTreeContObject(
			@Param("subscrObjectTreeId") Long subscrObjectTreeId);

}
