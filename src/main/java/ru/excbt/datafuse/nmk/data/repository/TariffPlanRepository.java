package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.TariffPlan;

public interface TariffPlanRepository extends CrudRepository<TariffPlan, Long> {

	/**
	 * 
	 * @param subscriberId
	 * @param rsoOrganizationId
	 * @return
	 */
	@Query("SELECT d FROM TariffPlan d "
			+ "WHERE d.subscriber.id = :subscriberId and d.contObject is null "
			+ "ORDER BY d.rso.organizationName, d.tariffOption.tariffOptionOrder, d.tariffType.tariffTypeOrder")
	public List<TariffPlan> selectDefaultTariffPlan(
			@Param("subscriberId") long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param rsoOrganizationId
	 * @return
	 */
	@Query("SELECT d FROM TariffPlan d "
			+ "WHERE d.subscriber.id = :subscriberId and d.rso.id = :rsoOrganizationId and d.contObject is null "
			+ "ORDER BY d.tariffOption.tariffOptionOrder, d.tariffType.tariffTypeOrder")
	public List<TariffPlan> selectDefaultTariffPlan(
			@Param("subscriberId") long subscriberId,
			@Param("rsoOrganizationId") long rsoOrganizationId);

	/**
	 * 
	 * @param subscriberId
	 * @param rsoOrganizationId
	 * @param contObjectId
	 * @return
	 */
	@Query("SELECT d FROM TariffPlan d "
			+ "WHERE d.subscriber.id = :subscriberId and d.rso.id = :rsoOrganizationId and d.contObject.id = :contObjectId "
			+ "ORDER BY d.tariffOption.tariffOptionOrder, d.tariffType.tariffTypeOrder")
	public List<TariffPlan> selectTariffPlan(
			@Param("subscriberId") long subscriberId,
			@Param("rsoOrganizationId") long rsoOrganizationId,
			@Param("contObjectId") long contObjectId);

	@Modifying
	@Query("DELETE TariffPlan d "
			+ "WHERE d.subscriber.id = :subscriberId AND d.rso.id = :rsoOrganizationId AND d.contObject.id IS NULL")
	public void deleteDefaultTariffPlan(
			@Param("subscriberId") long subscriberId,
			@Param("rsoOrganizationId") long rsoOrganizationId);

	@Query("SELECT d FROM TariffPlan d "
			+ "WHERE d.subscriber.id = :subscriberId and d.id = :tariffPlanId")
	public List<Long> selectTariffPlanId(
			@Param("subscriberId") long subscriberId,
			@Param("tariffPlanId") long tariffPlanId);

}
