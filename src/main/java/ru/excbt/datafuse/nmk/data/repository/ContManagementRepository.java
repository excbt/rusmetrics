package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContManagement;

public interface ContManagementRepository extends
		CrudRepository<ContManagement, Long> {

	@Query("SELECT cm FROM ContManagement cm INNER JOIN cm.organization o INNER JOIN cm.contObject co "
			+ "WHERE co.id = :contObjectId")
	public List<ContManagement> selectAllManagement(
			@Param("contObjectId") long contObjectId);

	@Query("SELECT cm FROM ContManagement cm INNER JOIN cm.organization o INNER JOIN cm.contObject co "
			+ "WHERE co.id = :contObjectId AND cm.endDate is null")
	public List<ContManagement> selectActiveManagement(
			@Param("contObjectId") long contObjectId);

	@Query("SELECT cm FROM ContManagement cm INNER JOIN cm.organization o "
			+ "WHERE o.id = :organizationId")
	public List<ContManagement> selectByOrganization(
			@Param("organizationId") long organizationId);

	@Query("SELECT cm FROM ContManagement cm INNER JOIN cm.organization org INNER JOIN cm.contObject co "
			+ " WHERE co.id = :contObjectId AND org.id = :organizationId")
	public List<ContManagement> selectContMagement(
			@Param("contObjectId") long contObjectId,
			@Param("organizationId") long organizationId);

}
