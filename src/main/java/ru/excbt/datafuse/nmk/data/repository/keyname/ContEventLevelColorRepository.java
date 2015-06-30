package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;

public interface ContEventLevelColorRepository extends
		JpaRepository<ContEventLevelColor, String> {

	@Query(value = "SELECT c FROM ContEventLevelColor c "
			+ " WHERE :eventLevel >= c.levelFrom AND :eventLevel <= c.levelTo ")
	public List<ContEventLevelColor> selectByContEventLevel(
			@Param("eventLevel") Integer eventLevel);
}
