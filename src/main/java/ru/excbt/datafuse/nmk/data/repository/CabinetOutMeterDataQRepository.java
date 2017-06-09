package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.excbt.datafuse.nmk.data.model.CabinetOutMeterDataQ;

import java.util.List;

/**
 * Created by kovtonyk on 08.06.2017.
 */
public interface CabinetOutMeterDataQRepository extends JpaRepository<CabinetOutMeterDataQ, CabinetOutMeterDataQ.QId> {

    @Query ("SELECT q FROM CabinetOutMeterDataQ q ORDER BY q.qId.qDateTime ASC")
    List<CabinetOutMeterDataQ> findAllOrOrderDateTime();
}
