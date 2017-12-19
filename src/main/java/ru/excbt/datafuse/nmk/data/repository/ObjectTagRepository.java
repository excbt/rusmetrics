package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ObjectTag;

import java.util.List;

public interface ObjectTagRepository extends JpaRepository<ObjectTag, ObjectTag.PK> {

    @Query("SELECT t FROM ObjectTag t WHERE t.subscriberId = :subscriberId AND t.objectTagKeyname = :objectTagKeyname")
    List<ObjectTag> findAllObjectsTags(@Param("subscriberId") Long subscriberId,
                                       @Param("objectTagKeyname") String objectTagKeyname);


    @Query("SELECT t FROM ObjectTag t WHERE t.subscriberId = :subscriberId AND t.objectTagKeyname = :objectTagKeyname " +
        "AND t.objectId = :objectId ")
    List<ObjectTag> findObjectTags(@Param("subscriberId") Long subscriberId,
                                                        @Param("objectTagKeyname") String objectTagKeyname,
                                                        @Param("objectId") Long objectId);


    @Query("SELECT DISTINCT t.tagName FROM ObjectTag t " +
        " WHERE t.subscriberId = :subscriberId AND t.objectTagKeyname = :objectTagKeyname" +
        " ORDER BY t.tagName ")
    List<String> findAllObjectTagNames(@Param("subscriberId") Long subscriberId,
                                       @Param("objectTagKeyname") String objectTagKeyname);



}
