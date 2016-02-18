package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.keyname.MeasureUnit;

/**
 * Repository для MeasureUnit
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.01.2015
 *
 */
public interface MeasureUnitRepository extends JpaRepository<MeasureUnit, String> {

	@Query("SELECT ma FROM MeasureUnit ma WHERE ma.measureCategory IN "
			+ " (SELECT mu.measureCategory FROM MeasureUnit mu WHERE mu.keyname = UPPER(:keyname)) ")
	public List<MeasureUnit> selectMeasureUnitsSame(@Param("keyname") String keyname);

}
