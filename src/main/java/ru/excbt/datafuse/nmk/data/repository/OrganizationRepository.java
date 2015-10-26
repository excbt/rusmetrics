package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.Organization;

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
