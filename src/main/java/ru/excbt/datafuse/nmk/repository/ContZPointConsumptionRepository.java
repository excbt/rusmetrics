package ru.excbt.datafuse.nmk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;

import java.time.LocalDateTime;

@Repository
public interface ContZPointConsumptionRepository extends JpaRepository<ContZPointConsumption, Long> {

    @Modifying
    @Query("DELETE FROM ContZPointConsumption c WHERE c.contZPointId = :contZPointId " +
        "AND c.destTimeDetailType = :destTimeDetailType AND c.dateFrom = :dateFrom")
    void deleteByKey (@Param("contZPointId") Long contZPointId,
                      @Param("destTimeDetailType") String destTimeDetailType,
                      @Param("dateFrom")LocalDateTime dateFrom);

}
