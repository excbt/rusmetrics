package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;
import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.support.DeviceMetadataInfo;
import ru.excbt.datafuse.nmk.data.model.support.EntityColumn;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointMetadataRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class ContZPointMetadataService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(ContZPointMetadataService.class);

	private static final List<String> HWATER_COLUMNS;
	private static final List<String> HWATER_SERVICES;

	static {
		HWATER_COLUMNS = Collections.unmodifiableList(
				Arrays.asList("t_in", "t_out", "t_cold", "t_outdoor", "m_in", "m_out", "m_delta", "v_in", "v_out",
						"v_delta", "h_in", "h_out", "h_delta", "p_in", "p_out", "p_delta", "work_time", "fail_time"));
		HWATER_SERVICES = Collections.unmodifiableList(Arrays.asList(ContServiceTypeKey.HEAT.getKeyname(),
				ContServiceTypeKey.CW.getKeyname(), ContServiceTypeKey.HW.getKeyname()));
	}

	private class ContZPointMetadataKey {
		private Long contZPointId;
		private Long deviceObjectId;
		private ContZPoint contZPoint;
		private DeviceObject deviceObject;
		private Integer tsNumber;
	}

	@Autowired
	private ContZPointMetadataRepository contZPointMetadataRepository;

	@Autowired
	private DeviceMetadataService deviceMetadataService;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private DeviceObjectDataSourceService deviceObjectDataSourceService;

    @Autowired
	private DBSessionService dbSessionService;

	/**
	 *
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPointMetadata> selectNewMetadata(Long contZPointId, boolean isTsFilter) {

		List<ContZPointMetadata> result = new ArrayList<>();

		ContZPointMetadataKey key = findContZPointMetadataKey(contZPointId);
		if (key == null) {
			return new ArrayList<>();
		}

		List<DeviceObjectDataSource> deviceDataSourceList = deviceObjectDataSourceService
				.selectActiveDeviceObjectDataSource(key.deviceObjectId);

		if (deviceDataSourceList.isEmpty() || deviceDataSourceList.size() > 1) {
			return result;
		}

		DeviceObjectDataSource deviceDataSource = deviceDataSourceList.get(0);

		String deviceMetadataType = deviceDataSource.getSubscrDataSource().getDataSourceType().getDeviceMetadataType();
		if (deviceMetadataType == null) {
			return new ArrayList<>();
		}

		Long deviceModelId = key.deviceObject.getDeviceModelId();

		List<DeviceMetadata> deviceMetadataList = deviceMetadataService.selectDeviceMetadata(deviceModelId,
				deviceMetadataType);

		List<DeviceMetadata> transformedMetadataList = deviceMetadataService
				.transformDeviceMetadata(deviceMetadataList);

		List<DeviceMetadata> tsMetadataList = transformedMetadataList.stream()
				.filter(i -> i.getMetaNumber() == null
						|| (isTsFilter == false || (key.tsNumber != null && i.getMetaNumber().equals(key.tsNumber))))
				.collect(Collectors.toList());

		result = contZPointMetadataFactory(tsMetadataList, key.contZPoint, key.deviceObject);

		return result;
	};

	/**
	 *
	 * @param src
	 * @return
	 */
	public ContZPointMetadata contZPointMetadataFactory(DeviceMetadata src, ContZPoint contZPoint,
			DeviceObject deviceObject) {

		ContZPointMetadata dst = new ContZPointMetadata();

		dst.setDeviceMetadataType(src.getDeviceMetadataType());
		dst.setSrcProp(src.getSrcProp());
		dst.setDestProp(src.getDestProp());
		dst.setIsIntegrator(src.getIsIntegrator());
		dst.setSrcPropDivision(src.getSrcPropDivision());
		dst.setDestPropCapacity(src.getDestPropCapacity());
		dst.setSrcMeasureUnit(src.getSrcMeasureUnit());
		dst.setDestMeasureUnit(src.getDestMeasureUnit());
		dst.setMetaNumber(src.getMetaNumber());
		dst.setMetaOrder(src.getMetaOrder());
		dst.setMetaDescription(src.getMetaDescription());
		dst.setMetaComment(src.getMetaComment());
		dst.setPropVars(src.getPropVars());
		dst.setPropFunc(src.getPropFunc());
		dst.setDestDbType(src.getDestDbType());
		dst.setMetaVersion(src.getMetaVersion());
		dst.setMetaName(src.getMetaName());

		if (contZPoint != null) {
			dst.setContZPoint(contZPoint);
			dst.setContZPointId(contZPoint.getId());
		}
		if (deviceObject != null) {
			dst.setDeviceObject(deviceObject);
			dst.setDeviceObjectId(deviceObject.getId());
		}

		return dst;
	}

	/**
	 *
	 * @param src
	 * @return
	 */
	public ContZPointMetadata contZPointMetadataFactory(DeviceMetadata src) {
		return contZPointMetadataFactory(src, null, null);
	}

	/**
	 *
	 * @param srcList
	 * @return
	 */
	public List<ContZPointMetadata> contZPointMetadataFactory(List<DeviceMetadata> srcList, ContZPoint contZPoint,
			DeviceObject deviceObject) {
		List<ContZPointMetadata> result = new ArrayList<>();
		for (DeviceMetadata m : srcList) {
			ContZPointMetadata contZPointMetadata = contZPointMetadataFactory(m, contZPoint, deviceObject);
			result.add(contZPointMetadata);
		}
		return result;
	}

	/**
	 *
	 * @param metadataList
	 * @return
	 */
	public List<EntityColumn> buildSrcProps(List<ContZPointMetadata> metadataList) {

		List<EntityColumn> result = metadataList.stream().filter(i -> i.getMetaNumber() != null).map(i -> {
			return new EntityColumn(i.getSrcProp());
		}).distinct().sorted().collect(Collectors.toList());

		return result;
	}

	/**
	 *
	 * @param metadataList
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<DeviceMetadataInfo> buildSrcPropsDeviceMapping(List<ContZPointMetadata> metadataList) {

		final Map<String, List<String>> deviceMappings = getSrcPropsDeviceMapping(metadataList);

		List<DeviceMetadataInfo> result = metadataList.stream().filter(i -> i.getMetaNumber() != null).map(i -> {
			List<String> mList = deviceMappings.get(i.getSrcProp());
			return new DeviceMetadataInfo(i.getSrcProp(), null, mList);
		}).distinct().sorted().collect(Collectors.toList());

		return result;
	}

	/**
	 *
	 * @param metadataList
	 * @return
	 */
	public List<EntityColumn> buildDestProps(List<ContZPointMetadata> metadataList) {

		List<EntityColumn> result = metadataList.stream().filter(i -> i.getMetaNumber() != null).map(i -> {
			return new EntityColumn(i.getDestProp(), i.getDestDbType());
		}).distinct().sorted().collect(Collectors.toList());

		return result;
	}

	/**
	 *
	 * @param contZPointId
	 * @return
	 */
	public List<EntityColumn> selectContZPointDestDB(Long contZPointId) {

		ContZPoint zpoint = contZPointService.findOne(contZPointId);

		if (zpoint != null && HWATER_SERVICES.contains(zpoint.getContServiceTypeKeyname())) {
			return HWATER_COLUMNS.stream().sorted().map(i -> {
				return new EntityColumn(i, "NUMERIC");
			}).collect(Collectors.toList());
		}

		return new ArrayList<>();
	}

	/**
	 *
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPointMetadata> selectContZPointMetadata(Long contZPointId) {
		ContZPointMetadataKey key = findContZPointMetadataKey(contZPointId);
		if (key == null) {
			logger.warn("No Metadata KEY Found");
			return new ArrayList<>();
		}

		logger.trace("SELECT contZPoint metadata for contZPointId: {}, deviceObjectId: {}", key.contZPointId,
				key.deviceObjectId);

		return contZPointMetadataRepository.selectContZPointMetadata(key.contZPointId, key.deviceObjectId);
	}

	/**
	 *
	 * @param contZPointId
	 * @return
	 */
	private ContZPointMetadataKey findContZPointMetadataKey(Long contZPointId) {

		ContZPoint zpoint = contZPointService.findOne(contZPointId);

		if (zpoint == null) {
			return null;
		}

		DeviceObject deviceObject = findDeviceObject(zpoint);
		if (deviceObject == null) {
			return null;
		}

		ContZPointMetadataKey result = new ContZPointMetadataKey();
		result.tsNumber = zpoint.getTsNumber();
		result.contZPoint = zpoint;
		result.contZPointId = zpoint.getId();
		result.deviceObject = deviceObject;
		result.deviceObjectId = deviceObject.getId();

		return result;

	}

	/**
	 *
	 * @param contZPoint
	 * @return
	 */
	private DeviceObject findDeviceObject(ContZPoint contZPoint) {
		if (contZPoint == null) {
			return null;
		}

		List<DeviceObject> deviceObjects = contZPointService.selectDeviceObjects(contZPoint.getId());
		if (deviceObjects.isEmpty() || deviceObjects.size() > 1) {
			return null;
		}

		DeviceObject deviceObject = deviceObjects.get(0);

		return deviceObject;
	}

	/**
	 *
	 * @param contZPointId
	 * @param deviceObjectId
	 * @return
	 */
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public int deleteOtherContZPointMetadata(Long contZPointId, Long deviceObjectId) {
		return contZPointMetadataRepository.deleteOtherContZPointMetadata(contZPointId, deviceObjectId);
	}

    /**
     *
     * @param entityList
     * @param contZPointId
     * @return
     */
	@Secured({ ROLE_ZPOINT_ADMIN, ROLE_RMA_ZPOINT_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<ContZPointMetadata> saveContZPointMetadata(List<ContZPointMetadata> entityList, Long contZPointId) {
		ContZPoint contZPoint = contZPointService.findOne(contZPointId);
		if (contZPoint == null) {
			throw new PersistenceException(String.format("ContZPoint (id=%d) is not found", contZPointId));
		}

		DeviceObject deviceObject = findDeviceObject(contZPoint);
		if (deviceObject == null) {
			throw new PersistenceException(
					String.format("ContZPoint (id=%d) DeviceObject is not configured properly", contZPointId));
		}

		for (ContZPointMetadata e : entityList) {
			e.setContZPoint(contZPoint);
			e.setDeviceObject(deviceObject);
		}

		List<ContZPointMetadata> result = Lists.newArrayList(contZPointMetadataRepository.save(entityList));

		deleteOtherContZPointMetadata(contZPointId, deviceObject.getId());

		return result;
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Map<String, List<String>> getSrcPropsDeviceMapping(List<ContZPointMetadata> metadataList) {

		Map<String, List<String>> result = null;

		List<Pair<String, String>> pairs = metadataList.stream()
				.filter(i -> i.getDeviceObjectId() != null && i.getDeviceMetadataType() != null)
				.map(i -> new ImmutablePair<>(i.getDeviceObjectId().toString(), i.getDeviceMetadataType()))
				.distinct().collect(Collectors.toList());

		if (pairs.size() == 1) {
			Pair<String, String> p = pairs.get(0);
			result = selectSrcPropsDeviceMapping(Long.valueOf(p.getLeft()), p.getRight());
		}

		return result;

	}

	/**
	 *
	 * @param deviceObjectId
	 * @param deviceMetadataType
	 * @return
	 */
	private Map<String, List<String>> selectSrcPropsDeviceMapping(Long deviceObjectId, String deviceMetadataType) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT src_prop, device_mapping, device_mapping_info ");
		sb.append(" FROM portal.v_device_object_metadata_info ");
		sb.append("WHERE device_object_id = :par_device_object_id AND ");
		sb.append(" device_metadata_type = :par_device_metadata_type ");

		Query qry = dbSessionService.getSession().createNativeQuery(sb.toString());
		qry.setParameter("par_device_object_id", deviceObjectId);
		qry.setParameter("par_device_metadata_type", deviceMetadataType);

		List<?> qryResult = qry.getResultList();

		final Map<String, List<String>> resultMap = new HashMap<>();
		for (Object row : qryResult) {
			if (row instanceof Object[]) {
				Object[] rowArray = (Object[]) row;
				String srcProp = (String) rowArray[0];
				String deviceMapping = (String) rowArray[1];
				String deviceMappingInfo = (String) rowArray[2];
				List<String> mList = resultMap.get(srcProp);
				if (mList == null) {
					mList = new ArrayList<>();
					mList.add(deviceMapping);
					mList.add(deviceMappingInfo);
					resultMap.put(srcProp, mList);
				} else {
					logger.warn("SrcProp {} already exists in deviceObjectId={}", srcProp, deviceObjectId);
				}
			}
		}
		return resultMap;

	}

}
