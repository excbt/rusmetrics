package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.SubscrPref;

public interface SubscrPrefRepository extends CrudRepository<SubscrPref, String> {

	@Query("SELECT sp FROM SubscrPref sp ORDER BY sp.prefOrder NULLS LAST")
	public List<SubscrPref> selectSubscrPref();
}
