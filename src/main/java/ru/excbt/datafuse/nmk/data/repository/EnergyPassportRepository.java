package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.EnergyPassport;

import java.util.List;

/**
 * Created by kovtonyk on 10.04.2017.
 */
@Repository
public interface EnergyPassportRepository extends JpaRepository<EnergyPassport, Long> {

    @Query("SELECT ep from EnergyPassport ep WHERE ep.subscriber.id = :subscriberId AND ep.deleted = 0")
    List<EnergyPassport> findBySubscriberId (@Param("subscriberId") Long subscriberId);

}
