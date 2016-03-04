package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectPkeLimit;

public interface DeviceObjectPkeLimitRepository extends JpaRepository<DeviceObjectPkeLimit, Long> {

}
