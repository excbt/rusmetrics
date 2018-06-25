package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.model.support.CityContObjects;
import ru.excbt.datafuse.nmk.data.model.support.CityContObjectsServiceTypeInfo;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectServiceTypeInfo;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceTypeInfoART;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.MeasureUnitKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.service.utils.DBRowUtil;

/**
 * Сервис по работе с вычисляемыми данными по горячей воде
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 03.08.2015
 *
 */
@Service
@Transactional( readOnly = true)
public class ContServiceDataHWaterDeltaService {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterDeltaService.class);

	@PersistenceContext
	private EntityManager em;

	private final ContObjectService contObjectService;


    private final ContObjectFiasService contObjectFiasService;

    private final ObjectAccessService objectAccessService;

    private final ContObjectMapper contObjectMapper;

    public ContServiceDataHWaterDeltaService(ContObjectService contObjectService, ContObjectFiasService contObjectFiasService, ObjectAccessService objectAccessService, ContObjectMapper contObjectMapper) {
        this.contObjectService = contObjectService;
        this.contObjectFiasService = contObjectFiasService;
        this.objectAccessService = objectAccessService;
        this.contObjectMapper = contObjectMapper;
    }

    /**
     *
     * @param subscriberId
     * @param ldp
     * @param contServiceTypeKey
     * @param timeDetailKey
     * @param contObjectId
     * @return
     */
	public List<Object[]> selectRawHWaterDeltaAgr(Long subscriberId, LocalDatePeriod ldp,
			ContServiceTypeKey contServiceTypeKey, TimeDetailKey timeDetailKey, Long contObjectId) {

		checkNotNull(subscriberId);
		checkNotNull(ldp);
		checkNotNull(contServiceTypeKey);
		checkNotNull(timeDetailKey);
		if (ldp.isInvalidEq()) {
			return null;
		}

		logger.trace("CALL STORED PROC (get_cont_object_hwater_delta_agr) with params");
		logger.trace("p_subscriber_id:{}", subscriberId);
		logger.trace("p_date_from:{}", ldp.getDateFrom());
		logger.trace("p_date_to:{}", ldp.getDateTo());
		logger.trace("p_cont_service_type:{}", contServiceTypeKey.getKeyname());
		logger.trace("p_time_detail_type:{}", timeDetailKey.getKeyname());
		logger.trace("p_cont_object_id:{}", contObjectId);

		String qString = "SELECT cont_object_id," // 0
				+ "sum_m_delta," // 1
				+ "sum_v_delta," // 2
				+ "sum_h_delta," // 3
				+ "avg_t_in," // 4
				+ "avg_t_out " // 5
				+ "FROM get_cont_object_hwater_delta_agr(" + ":p_subscriber_id, " + ":p_date_from, " + ":p_date_to, "
				+ ":p_cont_service_type, " + ":p_time_detail_type " + (contObjectId != null ? ",:p_cont_object_id" : "")
				+ ");";
		logger.debug("qString: {}", qString);

		Query query = em.createNativeQuery(qString).setParameter("p_subscriber_id", subscriberId)
				.setParameter("p_date_from", ldp.getDateFrom()).setParameter("p_date_to", ldp.getDateTo())
				.setParameter("p_cont_service_type", contServiceTypeKey.getKeyname())
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
	 * @param cityFiasUUID
	 * @return
	 */
	public List<Object[]> selectRawHWaterDeltaAgr_ByCity(Long subscriberId, LocalDatePeriod ldp,
			ContServiceTypeKey contServiceTypeKey, TimeDetailKey timeDetailKey, UUID cityFiasUUID) {

		checkNotNull(subscriberId);
		checkNotNull(ldp);
		checkNotNull(contServiceTypeKey);
		checkNotNull(timeDetailKey);
		checkNotNull(cityFiasUUID);
		if (ldp.isInvalidEq()) {
			return null;
		}

		logger.trace("CALL STORED PROC (get_cont_object_hwater_delta_agr_city) with params");
		logger.trace("p_subscriber_id:{}", subscriberId);
		logger.trace("p_date_from:{}", ldp.getDateFrom());
		logger.trace("p_date_to:{}", ldp.getDateTo());
		logger.trace("p_cont_service_type:{}", contServiceTypeKey.getKeyname());
		logger.trace("p_time_detail_type:{}", timeDetailKey.getKeyname());
		logger.trace("p_city_fias_str:{}", cityFiasUUID.toString());

		String qString = "SELECT cont_object_id," // 0
				+ "sum_m_delta," // 1
				+ "sum_v_delta," // 2
				+ "sum_h_delta," // 3
				+ "avg_t_in," // 4
				+ "avg_t_out " // 5
				+ "FROM get_cont_object_hwater_delta_agr_city(" + ":p_subscriber_id, " + ":p_date_from, "
				+ ":p_date_to, " + ":p_cont_service_type, " + ":p_time_detail_type, " + ":p_city_fias_str " + ");";
		logger.debug("qString: {}", qString);

		Query query = em.createNativeQuery(qString).setParameter("p_subscriber_id", subscriberId)
				.setParameter("p_date_from", ldp.getDateFrom()).setParameter("p_date_to", ldp.getDateTo())
				.setParameter("p_cont_service_type", contServiceTypeKey.getKeyname())
				.setParameter("p_time_detail_type", timeDetailKey.getKeyname())
				.setParameter("p_city_fias_str", cityFiasUUID.toString());

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
	protected Map<Long, ContServiceTypeInfoART> selectHWaterDeltaART(Long subscriberId, LocalDatePeriod ldp,
			ContServiceTypeKey contServiceTypeKey, TimeDetailKey timeDetailKey) {

		List<Object[]> contObjectHWaterDeltaAgr = selectRawHWaterDeltaAgr(subscriberId, ldp,
				contServiceTypeKey, timeDetailKey, null);

		Map<Long, ContServiceTypeInfoART> resultMap = processRawServiceTypeARTRecords(contServiceTypeKey,
				contObjectHWaterDeltaAgr);

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
	public Map<Long, ContServiceTypeInfoART> selectHWaterDeltaART(Long subscriberId, LocalDatePeriod ldp,
			ContServiceTypeKey contServiceTypeKey, TimeDetailKey timeDetailKey, Long contObjectId) {

		List<Object[]> contObjectHWaterDeltaAgr = selectRawHWaterDeltaAgr(subscriberId, ldp,
				contServiceTypeKey, timeDetailKey, contObjectId);

		Map<Long, ContServiceTypeInfoART> resultMap = processRawServiceTypeARTRecords(contServiceTypeKey,
				contObjectHWaterDeltaAgr);

		return resultMap;
	}

	/**
	 *
	 * @param subscriberId
	 * @param ldp
	 * @param contServiceTypeKey
	 * @param timeDetailKey
	 * @param cityFiasUUID
	 * @return
	 */
	public Map<Long, ContServiceTypeInfoART> selectHWaterDeltaART_ByCity(Long subscriberId,
			LocalDatePeriod ldp, ContServiceTypeKey contServiceTypeKey, TimeDetailKey timeDetailKey,
			UUID cityFiasUUID) {

		List<Object[]> contObjectHWaterDeltaAgr = selectRawHWaterDeltaAgr_ByCity(subscriberId, ldp,
				contServiceTypeKey, timeDetailKey, cityFiasUUID);

		Map<Long, ContServiceTypeInfoART> resultMap = processRawServiceTypeARTRecords(contServiceTypeKey,
				contObjectHWaterDeltaAgr);

		return resultMap;
	}

	/**
	 *
	 * @param dbResult
	 * @param contServiceTypeKey
	 * @return
	 */
	private Map<Long, ContServiceTypeInfoART> processRawServiceTypeARTRecords(ContServiceTypeKey contServiceTypeKey,
			List<Object[]> dbResult) {
		Map<Long, ContServiceTypeInfoART> resultMap = new HashMap<>();
		for (Object[] row : dbResult) {
			Long contObjectId = DBRowUtil.asLong(row[0]);
			ContServiceTypeInfoART art = new ContServiceTypeInfoART(contServiceTypeKey);
			if (contServiceTypeKey.getMeasureUnit() == MeasureUnitKey.V_M3) {
				art.setAbsConsValue(DBRowUtil.asDouble(row[2])); // sum_v_delta
			} else if (contServiceTypeKey.getMeasureUnit() == MeasureUnitKey.W_GCAL) {
				art.setAbsConsValue(DBRowUtil.asDouble(row[3])); // sum_h_delta
			}
			art.setTempValue(DBRowUtil.asDouble(row[4])); // avg_t_in
			resultMap.put(contObjectId, art);
		}

		return resultMap;
	}

    /**
     *
     * @param subscriberId
     * @param ldp
     * @param contObjectId
     * @return
     */
	public List<ContObjectServiceTypeInfo> getContObjectServiceTypeInfo(Long subscriberId, LocalDatePeriod ldp,
			Long contObjectId) {

		checkNotNull(subscriberId, "subscriberId is null");
		checkNotNull(subscriberId, "localDatePeriod is null");

		List<ContObject> contObjects = new ArrayList<>();

		if (contObjectId == null) {
			contObjects.addAll(objectAccessService.findContObjects(subscriberId));
		} else {
			ContObject contObject = contObjectService.findContObjectChecked(contObjectId);

			contObjects.add(contObject);
		}

		Map<Long, ContServiceTypeInfoART> hwContObjectARTs = selectHWaterDeltaART(subscriberId, ldp,
				ContServiceTypeKey.HW, TimeDetailKey.TYPE_24H, contObjectId);

		Map<Long, ContServiceTypeInfoART> heatContObjectARTs = selectHWaterDeltaART(subscriberId, ldp,
				ContServiceTypeKey.HEAT, TimeDetailKey.TYPE_24H, contObjectId);

		List<ContObjectServiceTypeInfo> resultList = processContObjectServiceTypeInfo(contObjects, hwContObjectARTs,
				heatContObjectARTs);

		return resultList;
	}

    /**
     *
     * @param subscriberId
     * @param ldp
     * @param cityFiasUUID
     * @return
     */
	public List<ContObjectServiceTypeInfo> getContObjectServiceTypeInfo_ByCity(Long subscriberId,
			LocalDatePeriod ldp, UUID cityFiasUUID) {

		checkNotNull(subscriberId, "subscriberId is null");
		checkNotNull(subscriberId, "localDatePeriod is null");
		checkNotNull(cityFiasUUID, "cityFiasUUID is null");

		List<ContObject> contObjects = new ArrayList<>();

		contObjects.addAll(objectAccessService.findContObjects(subscriberId));

		List<Long> contObjectIds = contObjects.stream().map(i -> i.getId()).collect(Collectors.toList());
		Map<Long, ContObjectFias> contObjectFiasMap = contObjectFiasService.selectContObjectsFiasMap(contObjectIds);

		List<ContObject> cityContObjects = contObjects.stream()
				.filter((i) -> contObjectFiasMap.get(i.getId()) != null
						&& cityFiasUUID.equals(contObjectFiasMap.get(i.getId()).getCityFiasUUID()))
				.collect(Collectors.toList());

		Map<Long, ContServiceTypeInfoART> hwContObjectARTs = selectHWaterDeltaART_ByCity(subscriberId, ldp,
				ContServiceTypeKey.HW, TimeDetailKey.TYPE_24H, cityFiasUUID);

		Map<Long, ContServiceTypeInfoART> heatContObjectARTs = selectHWaterDeltaART_ByCity(subscriberId, ldp,
				ContServiceTypeKey.HEAT, TimeDetailKey.TYPE_24H, cityFiasUUID);

		List<ContObjectServiceTypeInfo> resultList = processContObjectServiceTypeInfo(cityContObjects, hwContObjectARTs,
				heatContObjectARTs);

		return resultList;
	}

	/**
	 *
	 * @param contObjects
	 * @param hwContObjectARTs
	 * @param heatContObjectARTs
	 * @return
	 */
	private List<ContObjectServiceTypeInfo> processContObjectServiceTypeInfo(final List<ContObject> contObjects,
			final Map<Long, ContServiceTypeInfoART> hwContObjectARTs,
			final Map<Long, ContServiceTypeInfoART> heatContObjectARTs) {

		List<ContObjectServiceTypeInfo> resultList = new ArrayList<>();

		List<Long> contObjectIds = contObjects.stream().filter(i -> i.getId() != null).map(i -> i.getId())
				.collect(Collectors.toList());

		Map<Long, ContObjectFias> contObjectFiasMap = contObjectFiasService.selectContObjectsFiasMap(contObjectIds);
		Map<Long, ContObjectGeoPos> contObjectGeoPosMap = contObjectService.selectContObjectsGeoPosMap(contObjectIds);

		contObjects.forEach((contObject) -> {
			ContObjectServiceTypeInfo item = new ContObjectServiceTypeInfo(contObjectMapper.toDto(contObject),
					contObjectFiasMap.get(contObject.getId()), contObjectGeoPosMap.get(contObject.getId()));

			ContServiceTypeInfoART hwART = hwContObjectARTs.get(contObject.getId());
			if (hwART != null) {
				item.addServiceTypeART(hwART);
			}

			ContServiceTypeInfoART heatART = heatContObjectARTs.get(contObject.getId());
			if (heatART != null) {
				item.addServiceTypeART(heatART);
			}

			resultList.add(item);
		});

		return resultList;
	}

	/**
	 *
	 * @param subscriberId
	 * @param ldp
	 * @return
	 */
	public List<CityContObjectsServiceTypeInfo> getAllCityMapContObjectsServiceTypeInfo(Long subscriberId,
			LocalDatePeriod ldp) {

		List<ContObjectServiceTypeInfo> allInfo = getContObjectServiceTypeInfo(subscriberId, ldp, null);
		List<CityContObjectsServiceTypeInfo> result = CityContObjects.makeCityContObjects(allInfo,
				CityContObjectsServiceTypeInfo.FACTORY_INSTANCE);
		return result;
	}

	/**
	 *
	 * @param subscriberId
	 * @param ldp
	 * @param cityFiasUUID
	 * @return
	 */
	public List<CityContObjectsServiceTypeInfo> getOneCityMapContObjectsServiceTypeInfo(Long subscriberId,
			LocalDatePeriod ldp, UUID cityFiasUUID) {

		List<ContObjectServiceTypeInfo> allInfo = getContObjectServiceTypeInfo_ByCity(subscriberId, ldp,
				cityFiasUUID);

		List<CityContObjectsServiceTypeInfo> result = CityContObjects.makeCityContObjects(allInfo,
				CityContObjectsServiceTypeInfo.FACTORY_INSTANCE);

		return result;
	}

}
