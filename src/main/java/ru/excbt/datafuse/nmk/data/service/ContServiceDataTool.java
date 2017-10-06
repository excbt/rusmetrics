package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.service.utils.ColumnHelper;

/**
 * Шаблон класса доступа к данным
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.12.2015
 *
 */
public class ContServiceDataTool {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataTool.class);

    private ContServiceDataTool() {
    }

    /**
     *
     * @param contZPointId
     * @param timeDetail
     * @param period
     * @param columnHelper
     * @param entityClass
     * @param session
     * @return
     */
	public static final Object[] serviceDataCustomQuery(Long contZPointId, TimeDetailKey timeDetail, LocalDatePeriod period,
                                              ColumnHelper columnHelper, Class<?> entityClass, Session session) {

		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" SELECT ");
		sqlString.append(columnHelper.build());
		sqlString.append(" FROM ");
		sqlString.append(entityClass.getSimpleName());
		sqlString.append(" d ");
		sqlString.append(" WHERE d.timeDetailType = :timeDetailType ");
		sqlString.append(" AND d.contZPoint.id = :contZPointId ");
		sqlString.append(" AND d.dataDate >= :beginDate ");
		sqlString.append(" AND d.dataDate <= :endDate ");
		logger.debug("Sql: {}", sqlString.toString());

		Query q1 = session.createQuery(sqlString.toString());

		q1.setParameter("timeDetailType", timeDetail.getKeyname());
		q1.setParameter("contZPointId", contZPointId);
		q1.setParameter("beginDate", period.getDateFrom());
		q1.setParameter("endDate", period.getDateTo());

		Object[] results = (Object[]) q1.getSingleResult();
		checkNotNull(results);

		return results;
	}

	/**
	 *
	 * @param list
	 * @return
	 */
	public static final <T> T getFirstElement(List<T> list) {
		checkNotNull(list);
		return list.isEmpty() ? null : list.get(0);
	};

	/**
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static final BigDecimal processDelta(BigDecimal a, BigDecimal b) {
		return a == null || b == null ? null : b.subtract(a);
	}

	public static final Double processDelta(Double a, Double b) {
		return a == null || b == null ? null : (b - a);
	}

}
