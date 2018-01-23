package ru.excbt.datafuse.nmk.repository.support;

import com.querydsl.core.types.dsl.EntityPathBase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface ExConsumptionRepositoryRI<T, P extends EntityPathBase<T>, ID extends Serializable> extends ExCustomRepository<T, P, ID> {

    /**
     *
     * @param timeDetailType
     * @param dateFrom
     * @param dateTo
     * @param contZPointId
     * @return
     */
    @Query("SELECT d FROM #{#entityName} d "
        + " WHERE time_detail_type = :timeDetailType AND d.deleted = 0 " +
        " AND d.dataDate BETWEEN :dateFrom AND :dateTo AND (:contZPointId IS NULL OR d.contZPointId = :contZPointId)")
    List<T> selectForConsumptionAny(
        @Param("timeDetailType") String timeDetailType,
        @Param("dateFrom") Date dateFrom,
        @Param("dateTo") Date dateTo,
        @Param("contZPointId") Long contZPointId);

    /**
     *
     * @param contZPointId
     * @param timeDetailType
     * @param dateFrom
     * @param dateTo
     * @return
     */
    @Query("SELECT d FROM #{#entityName} d "
        + " WHERE d.contZPointId = :contZPointId AND time_detail_type = :timeDetailType AND d.deleted = 0 " +
        " AND d.dataDate BETWEEN :dateFrom AND :dateTo")
    List<T> selectForConsumptionOne(@Param("contZPointId") long contZPointId,
                                    @Param("timeDetailType") String timeDetailType,
                                    @Param("dateFrom") Date dateFrom,
                                    @Param("dateTo") Date dateTo);


}
