package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectServiceTypeInfo;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceTypeInfoART;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.MeasureUnit;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.support.DBRowUtils;

@Service
@Transactional(readOnly = true)
public class ContObjectHWaterDeltaService {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectHWaterDeltaService.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ContObjectService contObjectService;

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
	public Map<Long, ContServiceTypeInfoART> selectContObjectHWaterDeltaART(
			Long subscriberId, LocalDatePeriod ldp,
			ContServiceTypeKey contServiceTypeKey, TimeDetailKey timeDetailKey) {

		List<Object[]> contObjectHWaterDeltaAgr = selectContObjectHWaterDeltaAgr(
				subscriberId, ldp, contServiceTypeKey, timeDetailKey);

		Map<Long, ContServiceTypeInfoART> resultMap = getServiceTypeARTRecords(
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
	public Map<Long, ContServiceTypeInfoART> selectContObjectHWaterDeltaART(
			Long subscriberId, LocalDatePeriod ldp,
			ContServiceTypeKey contServiceTypeKey, TimeDetailKey timeDetailKey,
			Long contObjectId) {

		List<Object[]> contObjectHWaterDeltaAgr = selectContObjectHWaterDeltaAgr(
				subscriberId, ldp, contServiceTypeKey, timeDetailKey,
				contObjectId);

		Map<Long, ContServiceTypeInfoART> resultMap = getServiceTypeARTRecords(
				contServiceTypeKey, contObjectHWaterDeltaAgr);

		return resultMap;
	}

	/**
	 * 
	 * @param dbResult
	 * @param contServiceTypeKey
	 * @return
	 */
	private Map<Long, ContServiceTypeInfoART> getServiceTypeARTRecords(
			ContServiceTypeKey contServiceTypeKey, List<Object[]> dbResult) {
		Map<Long, ContServiceTypeInfoART> resultMap = new HashMap<>();
		for (Object[] row : dbResult) {
			Long contObjectId = DBRowUtils.asLong(row[0]);
			ContServiceTypeInfoART art = new ContServiceTypeInfoART(
					contServiceTypeKey);
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

	/**
	 * 
	 * @param subscriberId
	 * @param ldp
	 * @param contServiceTypeKey
	 * @param timeDetailKey
	 * @return
	 */
	public List<ContObjectServiceTypeInfo> getContObjectServiceTypeInfo(
			Long subscriberId, LocalDatePeriod ldp) {

		List<ContObject> subscriberContObjects = contObjectService
				.selectSubscriberContObjects(subscriberId);

		subscriberContObjects.stream().collect(
				Collectors.toMap(ContObject::getId, (i) -> i));

		List<ContObjectServiceTypeInfo> resultList = new ArrayList<>();

		Map<Long, ContServiceTypeInfoART> hwContObjectARTs = selectContObjectHWaterDeltaART(
				subscriberId, ldp, ContServiceTypeKey.HW, TimeDetailKey.TYPE_1H);

		Map<Long, ContServiceTypeInfoART> heatContObjectARTs = selectContObjectHWaterDeltaART(
				subscriberId, ldp, ContServiceTypeKey.HEAT,
				TimeDetailKey.TYPE_1H);

		subscriberContObjects.forEach((contObject) -> {
			ContObjectServiceTypeInfo item = new ContObjectServiceTypeInfo(
					contObject);

			{
				ContServiceTypeInfoART hwART = hwContObjectARTs.get(contObject
						.getId());
				if (hwART != null) {

//					if (hwART.getAbsConsValue() != null
//							&& contObject.getHeatArea() != null) {
//						BigDecimal relValue = hwART.getAbsConsValue().divide(
//								contObject.getHeatArea());
//						hwART.setRelConsValue(relValue);
//					}

					item.addServiceTypeART(hwART);
				}

			}
			{
				ContServiceTypeInfoART heatART = heatContObjectARTs
						.get(contObject.getId());
				if (heatART != null) {
//					if (heatART.getAbsConsValue() != null
//							&& contObject.getHeatArea() != null) {
//						BigDecimal relValue = heatART.getAbsConsValue().divide(
//								contObject.getHeatArea());
//						heatART.setRelConsValue(relValue);
//					}

					item.addServiceTypeART(heatART);
				}
			}

			resultList.add(item);
		});

		return resultList;
	}

}
