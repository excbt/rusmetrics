package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.Organization;

/**
 * Repository для Organization
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.03.2015
 *
 */
public interface OrganizationRepository extends CrudRepository<Organization, Long> {

	@Query("SELECT o FROM Organization o WHERE o.flagRso = true ORDER BY o.organizationFullName")
	public List<Organization> selectRsoOrganizations();

	@Query("SELECT o FROM Organization o WHERE o.flagRma = true ORDER BY o.organizationFullName")
	public List<Organization> selectRmaOrganizations();

	@Query("SELECT o FROM Organization o WHERE o.flagCm = true ORDER BY o.organizationFullName")
	public List<Organization> selectCmOrganizations();

	@Query("SELECT o FROM Organization o ORDER BY o.organizationFullName")
	public List<Organization> selectOrganizations();

	public List<Organization> findByKeyname(String keyname);
}
