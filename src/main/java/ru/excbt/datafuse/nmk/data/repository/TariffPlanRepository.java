package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.TariffPlan;

/**
 * Repository для TariffPlan
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.04.2015
 *
 */
public interface TariffPlanRepository extends CrudRepository<TariffPlan, Long> {

	/**
	 * 
	 * @param subscriberId
	 * @param rsoOrganizationId
	 * @return
	 */
	@Query("SELECT d FROM TariffPlan d " + "WHERE d.subscriberId = :subscriberId "
			+ "ORDER BY d.tariffType.tariffTypeName, d.isDefault DESC, d.tariffPlanValue")
	public List<TariffPlan> selectTariffPlanList(@Param("subscriberId") long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param rsoOrganizationId
	 * @return
	 */
	@Query("SELECT d FROM TariffPlan d " + "WHERE d.subscriberId = :subscriberId and d.rso.id = :rsoOrganizationId "
			+ "ORDER BY d.tariffType.tariffTypeName, d.isDefault DESC, d.tariffPlanValue")
	public List<TariffPlan> selectTariffPlanList(@Param("subscriberId") long subscriberId,
			@Param("rsoOrganizationId") long rsoOrganizationId);

	/**
	 * 
	 * @param subscriberId
	 * @param rsoOrganizationId
	 * @param contObjectId
	 * @return
	 */
	// @Query("SELECT d FROM TariffPlan d "
	// +
	// "WHERE d.subscriber.id = :subscriberId and d.rso.id = :rsoOrganizationId
	// and d.contObject.id = :contObjectId "
	// +
	// "ORDER BY d.tariffOption.tariffOptionOrder,
	// d.tariffType.tariffTypeOrder")
	// public List<TariffPlan> selectTariffPlan(
	// @Param("subscriberId") long subscriberId,
	// @Param("rsoOrganizationId") long rsoOrganizationId,
	// @Param("contObjectId") long contObjectId);

	@Modifying
	@Query("DELETE TariffPlan d " + "WHERE d.subscriberId = :subscriberId AND d.rso.id = :rsoOrganizationId ")
	public void deleteTariffPlan(@Param("subscriberId") long subscriberId,
			@Param("rsoOrganizationId") long rsoOrganizationId);

	@Query("SELECT d FROM TariffPlan d " + "WHERE d.subscriberId = :subscriberId and d.id = :tariffPlanId")
	public List<Long> selectTariffPlanId(@Param("subscriberId") long subscriberId,
			@Param("tariffPlanId") long tariffPlanId);

	@Query("SELECT d.contObjects FROM TariffPlan d " + "WHERE d.subscriberId = :subscriberId and d.id = :tariffPlanId")
	public List<ContObject> selectTariffPlanContObjects(@Param("subscriberId") long subscriberId,
			@Param("tariffPlanId") long tariffPlanId);

	@Query("SELECT d.contObjects FROM TariffPlan d "
			+ " WHERE d.id = :tariffPlanId AND d.subscriberId = :subscriberId ")
	public List<ContObject> selectContObjects(@Param("subscriberId") long subscriberId,
			@Param("tariffPlanId") long tariffPlanId);

	@Query("SELECT d.contObjects FROM TariffPlan d " + " WHERE d.id = :tariffPlanId ")
	public List<ContObject> selectContObjects(@Param("tariffPlanId") long tariffPlanId);

	@Query("SELECT sco.contObject FROM SubscrContObject sco " + "WHERE sco.subscriberId = :subscriberId AND NOT EXISTS "
			+ "( SELECT dco.id FROM TariffPlan d LEFT JOIN d.contObjects dco WHERE d.id = :tariffPlanId AND sco.contObjectId = dco.id ) "
			+ "ORDER BY sco.contObject.name, sco.contObject.id")
	public List<ContObject> selectAvailableContObjects(@Param("subscriberId") long subscriberId,
			@Param("tariffPlanId") long tariffPlanId);

	@Query("SELECT d FROM TariffPlan d " + " WHERE d.subscriberId = :subscriberId AND "
			+ " NOT EXISTS (SELECT co FROM TariffPlan i INNER JOIN i.contObjects co WHERE i.id = d.id) "
			+ " ORDER BY d.isDefault DESC, d.tariffType.tariffTypeName, d.tariffPlanValue ")
	public List<TariffPlan> selectTariffPlanNoContObjects(@Param("subscriberId") long subscriberId);

}
