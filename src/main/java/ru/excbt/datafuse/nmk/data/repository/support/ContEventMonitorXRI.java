package ru.excbt.datafuse.nmk.data.repository.support;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.ContEventMonitorX;

import java.util.List;

public interface ContEventMonitorXRI<T extends ContEventMonitorX> {

    /**
     *
     * @param contObjectIds
     * @return
     */
    @Query(" SELECT m FROM #{#entityName} m " + "WHERE m.contObjectId IN (:contObjectIds) " +
        " AND (m.isScalar IS NULL OR m.isScalar = FALSE) ")
    List<T> selectByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);

    /**
     *
     * @param contZPointIds
     * @return
     */
    @Query(" SELECT m FROM #{#entityName} m " + "WHERE m.contZPointId IN (:contZPointIds) " +
        " AND (m.isScalar IS NULL OR m.isScalar = FALSE) ")
    List<T> selectByContZPointIds(@Param("contZPointIds") List<Long> contZPointIds);

    /**
     *
     * @param subscriberId
     * @return
     */
    @Query("SELECT m FROM #{#entityName} m " + "WHERE m.contZPointId IN ( " +
        "SELECT coa.contZPointId FROM ContZPointAccess coa WHERE coa.subscriberId = :subscriberId AND coa.accessTtlTz IS NULL) " +
        "AND (m.isScalar IS NULL OR m.isScalar = FALSE)")
    List<T> selectBySubscriberId(@Param("subscriberId") Long subscriberId);


    /**
     *
     * @param contObjectId
     * @return
     */
    @Query(" SELECT m FROM #{#entityName} m " +
        " WHERE m.contObjectId = :contObjectId AND (m.isScalar IS NULL OR m.isScalar = FALSE) " +
        " ORDER BY m.contEventTime ")
    List<T> selectByContObjectId(@Param("contObjectId") Long contObjectId);


    /**
     *
     * @param contObjectId
     * @param contZPointId
     * @return
     */
    @Query(" SELECT m FROM #{#entityName} m " +
        " WHERE m.contObjectId = :contObjectId AND (m.isScalar IS NULL OR m.isScalar = FALSE) " +
        " AND m.contZPointId = :contZPointId " + " ORDER BY m.contEventTime ")
    List<T> selectByZPointId(@Param("contObjectId") Long contObjectId,
                             @Param("contZPointId") Long contZPointId);


}
