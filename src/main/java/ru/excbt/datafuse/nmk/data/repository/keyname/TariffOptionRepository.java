package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.TariffOption;

/**
 * Repository для TariffOption
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.04.2015
 *
 */
public interface TariffOptionRepository extends CrudRepository<TariffOption, String> {

	@Query("SELECT to FROM TariffOption to ORDER BY to.tariffOptionOrder")
	public List<TariffOption> selectAll();
}
