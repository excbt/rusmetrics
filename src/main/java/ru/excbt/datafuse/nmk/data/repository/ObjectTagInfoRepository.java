package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.ObjectTagInfo;

import java.util.List;

@Repository
public interface ObjectTagInfoRepository extends JpaRepository<ObjectTagInfo, ObjectTagInfo.PK> {

    List<ObjectTagInfo> findBySubscriberId (Long subscriberId);

}
