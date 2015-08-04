package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.ServiceTypeInfoART;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.MeasureUnit;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.support.DBRowUtils;

@Service
@Transactional(readOnly = true)
public class ContObjectHWaterService {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectHWaterService.class);

	@PersistenceContext
	private EntityManager em;

	/**
	 * 
	 * @param subscriberId
	 * @param ldp
	 * @param contServiceType
	 * @param timeDetailType
	 * @return
	 */
	public List<Object[]> selectContObjectHWaterDeltaAgr(Long subscriberId,
			LocalDatePeriod ldp, ContServiceTypeKey contServiceTypeKey,
			TimeDetailKey timeDetailKey, Long contObjectId) {

		checkNotNull(subscriberId);
		checkNotNull(ldp);
		checkNotNull(contServiceTypeKey);
		checkNotNull(timeDetailKey);
		if (ldp.isInvalidEq()) {
			return null;
		}

		String qString = "SELECT cont_object_id," // 0
				+ "sum_m_delta," // 1
				+ "sum_v_delta," // 2
				+ "sum_h_delta," // 3
				+ "avg_t_in," // 4
				+ "avg_t_out " // 5
				+ "FROM get_cont_object_hwater_delta_agr("
				+ ":p_subscriber_id, " + ":p_date_from, "
				+ ":p_date_to, "
				+ ":p_cont_service_type, "
				+ ":p_time_detail_type "
				+ (contObjectId != null ? ",:p_cont_object_id" : "") + ");";
		logger.debug("qString: {}", qString);

		Query query = em
				.createNativeQuery(qString)
				.setParameter("p_subscriber_id", subscriberId)
				.setParameter("p_date_from", ldp.getDateFrom())
				.setParameter("p_date_to", ldp.getDateTo())
				.setParameter("p_cont_service_type",
						contServiceTypeKey.getKeyname())
				.setParameter("p_time_detail_type", timeDetailKey.getKeyname());

		if (contObjectId != null) {
			query.setParameter("p_cont_object_id", contObjectId);
		}

		List<?> resultList = query.getResultList();

		@SuppressWarnings("unchecked")
		List<Object[]> result = (List<Object[]>) resultList;

		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @param ldp
	 * @param contServiceTypeKey
	 * @param timeDetailKey
	 * @return
	 */
	public List<Object[]> selectContObjectHWaterDeltaAgr(Long subscriberId,
			LocalDatePeriod ldp, ContServiceTypeKey contServiceTypeKey,
			TimeDetailKey timeDetailKey) {
		return selectContObjectHWaterDeltaAgr(subscriberId, ldp,
				contServiceTypeKey, timeDetailKey, null);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param ldp
	 * @param contServiceType
	 * @param timeDetailType
	 * @return
	 */
	public Map<Long, ServiceTypeInfoART> selectContObjectHWaterDeltaART(
			Long subscriberId, LocalDatePeriod ldp,
			ContServiceTypeKey contServiceTypeKey, TimeDetailKey timeDetailKey) {

		List<Object[]> contObjectHWaterDeltaAgr = selectContObjectHWaterDeltaAgr(
				subscriberId, ldp, contServiceTypeKey, timeDetailKey);

		Map<Long, ServiceTypeInfoART> resultMap = getServiceTypeARTRecords(
				contServiceTypeKey, contObjectHWaterDeltaAgr);

		return resultMap;
	}

	/**
	 * 
	 * @param subscriberId
	 * @param ldp
	 * @param contServiceTypeKey
	 * @param timeDetailKey
	 * @return
	 */
	public Map<Long, ServiceTypeInfoART> selectContObjectHWaterDeltaART(
			Long subscriberId, LocalDatePeriod ldp,
			ContServiceTypeKey contServiceTypeKey, TimeDetailKey timeDetailKey,
			Long contObjectId) {

		List<Object[]> contObjectHWaterDeltaAgr = selectContObjectHWaterDeltaAgr(
				subscriberId, ldp, contServiceTypeKey, timeDetailKey,
				contObjectId);

		Map<Long, ServiceTypeInfoART> resultMap = getServiceTypeARTRecords(
				contServiceTypeKey, contObjectHWaterDeltaAgr);

		return resultMap;
	}

	/**
	 * 
	 * @param dbResult
	 * @param contServiceTypeKey
	 * @return
	 */
	private Map<Long, ServiceTypeInfoART> getServiceTypeARTRecords(
			ContServiceTypeKey contServiceTypeKey, List<Object[]> dbResult) {
		Map<Long, ServiceTypeInfoART> resultMap = new HashMap<>();
		for (Object[] row : dbResult) {
			Long contObjectId = DBRowUtils.asLong(row[0]);
			ServiceTypeInfoART art = new ServiceTypeInfoART(contServiceTypeKey);
			if (contServiceTypeKey.getMeasureUnit() == MeasureUnit.V_M3) {
				art.setAbsConsValue(DBRowUtils.asBigDecimal(row[2])); // sum_v_delta
			} else if (contServiceTypeKey.getMeasureUnit() == MeasureUnit.W_GCAL) {
				art.setAbsConsValue(DBRowUtils.asBigDecimal(row[3])); // sum_h_delta
			}
			art.setTempValue(DBRowUtils.asBigDecimal(row[4])); // avg_t_in
			resultMap.put(contObjectId, art);
		}

		return resultMap;
	}

}
