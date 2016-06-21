package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.RawModemModel;

public interface RawModemModelRepository extends CrudRepository<RawModemModel, Long> {

	/**
	 * 
	 * @return
	 */
	@Query("SELECT rm FROM RawModemModel rm ORDER BY rawModemModelName")
	public List<RawModemModel> selectRawModels();

}
