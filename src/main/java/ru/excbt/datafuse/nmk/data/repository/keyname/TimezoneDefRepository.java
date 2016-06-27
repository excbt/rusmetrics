package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;

/**
 * Repository для TimezoneDef
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.07.2015
 *
 */
public interface TimezoneDefRepository extends JpaRepository<TimezoneDef, String> {

	public List<TimezoneDef> findByIsDefault(Boolean isDeafult);

	@Query("SELECT tzd FROM TimezoneDef tzd ORDER BY tzd.timezoneAtOffset NULLS LAST, tzd.caption")
	public List<TimezoneDef> selectAll();

}
