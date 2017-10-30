package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.Organization;

/**
 * Repository для Organization
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.03.2015
 *
 */
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

	/**
	 *
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT o FROM Organization o WHERE o.flagRso = true AND (o.rmaSubscriberId = :rmaSubscriberId OR o.isCommon = TRUE) "
			+ " ORDER BY o.organizationFullName")
	List<Organization> selectRsoOrganizations(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 *
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT o FROM Organization o WHERE o.flagRma = true AND (o.rmaSubscriberId = :rmaSubscriberId OR o.isCommon = TRUE) "
			+ " ORDER BY o.organizationFullName")
	List<Organization> selectRmaOrganizations(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 *
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT o FROM Organization o WHERE o.flagCm = true AND (o.rmaSubscriberId = :rmaSubscriberId OR o.isCommon = TRUE) "
			+ " ORDER BY o.organizationFullName")
	List<Organization> selectCmOrganizations(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 *
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT o FROM Organization o WHERE (o.rmaSubscriberId = :rmaSubscriberId OR o.isCommon = TRUE) " +
        " AND o.deleted = 0 " +
		" ORDER BY o.organizationFullName")
	List<Organization> findOrganizationsOfRma(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 *
	 * @param rmaSubscriberId
	 * @param keyname
	 * @return
	 */
	@Query("SELECT o FROM Organization o WHERE (o.rmaSubscriberId = :rmaSubscriberId OR o.isCommon = TRUE) AND"
			+ " o.keyname = :keyname " + " ORDER BY o.organizationFullName")
	List<Organization> selectByKeyname(@Param("rmaSubscriberId") Long rmaSubscriberId,
			@Param("keyname") String keyname);

	/**
	 *
	 * @param oranizationIds
	 * @return
	 */
	@Query("SELECT o FROM Organization o WHERE (o.id in :oranizationIds)")
	List<Organization> selectByIds(@Param("oranizationIds") List<Long> oranizationIds);


}
