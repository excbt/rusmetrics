package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategory;

public interface ContEventCategoryRepository extends JpaRepository<ContEventCategory, String> {

	@Query("SELECT c FROM ContEventCategory c ORDER BY c.categoryOrder")
	public List<ContEventCategory> selectCategoryList();

}
