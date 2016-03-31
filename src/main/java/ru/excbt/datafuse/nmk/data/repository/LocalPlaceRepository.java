package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.LocalPlace;

public interface LocalPlaceRepository extends CrudRepository<LocalPlace, Long> {

	@Query("SELECT lp FROM LocalPlace lp WHERE (lp.isDisabled = null OR lp.isDisabled = FALSE)  AND lp.deleted = 0 "
			+ " ORDER BY lp.localPlaceName NULLS LAST")
	public List<LocalPlace> selectLocalPlaces();

	@Query("SELECT lp FROM LocalPlace lp WHERE (lp.isDisabled = null OR lp.isDisabled = FALSE)  AND lp.deleted = 0 "
			+ " AND lp.fiasUuid = :fiasUUID " + " ORDER BY lp.localPlaceName NULLS LAST")
	public List<LocalPlace> selectLocalPlacesByFias(@Param("fiasUUID") UUID fiasUUID);

}
