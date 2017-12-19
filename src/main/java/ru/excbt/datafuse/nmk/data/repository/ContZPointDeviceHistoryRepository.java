package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointDeviceHistory;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ContZPointDeviceHistoryRepository extends JpaRepository<ContZPointDeviceHistory, Long> {

    @Query("SELECT h FROM ContZPointDeviceHistory h WHERE h.contZPoint = ?1 AND h.endDate IS NULL ORDER BY h.endDate DESC NULLS FIRST")
    List<ContZPointDeviceHistory> findLastByContZPoint(ContZPoint zPoint, Pageable pageable);

    @Query("SELECT h FROM ContZPointDeviceHistory h WHERE h.contZPoint = ?1 ORDER BY h.revision DESC NULLS FIRST")
    List<ContZPointDeviceHistory> findAllByContZPoint(ContZPoint zPoint);

    Stream<ContZPointDeviceHistory> findTop1ByContZPointAndEndDateIsNullOrderByStartDateDesc(ContZPoint zPoint);

}

