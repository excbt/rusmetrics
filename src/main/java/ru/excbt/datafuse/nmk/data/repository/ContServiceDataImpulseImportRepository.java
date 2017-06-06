package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataImpulseImport;

/**
 * Created by kovtonyk on 06.06.2017.
 */
public interface ContServiceDataImpulseImportRepository extends JpaRepository<ContServiceDataImpulseImport, Long> {

    @Modifying
    @Procedure(name = "importImpulseData")
    void processImport(@Param("par_session_uuid") String sessionUUID);

}
