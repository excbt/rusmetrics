package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SessionDetailTypeContServiceType;
import ru.excbt.datafuse.nmk.data.model.keyname.SessionDetailType;

public interface SessionDetailTypeContServiceTypeRepository
		extends JpaRepository<SessionDetailTypeContServiceType, Long> {

	/**
	 * 
	 * @param contServiceType
	 * @return
	 */
	@Query("SELECT d.sessionDetailType FROM SessionDetailTypeContServiceType d "
			+ " WHERE d.contServiceType = :contServiceType ORDER BY d.orderIdx ")
	public List<SessionDetailType> selectSessionDetailType(@Param("contServiceType") String contServiceType);

	/**
	 * 
	 * @param contServiceType
	 * @return
	 */
	@Query("SELECT d FROM SessionDetailTypeContServiceType d "
			+ " WHERE d.contServiceType = :contServiceType ORDER BY d.orderIdx ")
	public List<SessionDetailTypeContServiceType> selectSessionDetailTypeContServiceType(
			@Param("contServiceType") String contServiceType);

	/**
	 * 
	 * @param contServiceType
	 * @return
	 */
	@Query("SELECT d FROM SessionDetailTypeContServiceType d "
			+ " WHERE d.contServiceType IN (:contServiceTypes) ORDER BY d.orderIdx ")
	public List<SessionDetailTypeContServiceType> selectSessionDetailTypeContServiceType(
			@Param("contServiceTypes") List<String> contServiceTypes);

}
