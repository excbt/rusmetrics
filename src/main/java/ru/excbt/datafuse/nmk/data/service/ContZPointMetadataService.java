package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;
import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;
import ru.excbt.datafuse.nmk.data.model.support.EntityColumn;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointMetadataRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.MeasureUnitRepository;

@Service
public class ContZPointMetadataService {

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
	private MeasureUnitRepository measureUnitRepository;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private DeviceObjectDataSourceService deviceObjectDataSourceService;

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPointMetadata> selectNewMetadata(Long contZPointId) {

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

		Long deviceModelId = key.deviceObject.getDeviceModelId();

		List<DeviceMetadata> deviceMetadataList = deviceMetadataService.selectDeviceMetadata(deviceModelId,
				deviceMetadataType);

		List<DeviceMetadata> transformedMetadataList = deviceMetadataService
				.transformDeviceMetadata(deviceMetadataList);

		List<DeviceMetadata> tsMetadataList = transformedMetadataList.stream()
				.filter(i -> i.getMetaNumber() == null
						|| (key.tsNumber != null && i.getMetaNumber().equals(key.tsNumber)))
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
		}).sorted().collect(Collectors.toList());

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
		}).sorted().collect(Collectors.toList());

		return result;
	}

	/**
	 * 
	 * @param contZPointId
	 * @return
	 */
	public List<EntityColumn> selectContZPointDestColumns(Long contZPointId) {

		ContZPoint zpoint = contZPointService.findOne(contZPointId);

		if (zpoint != null && HWATER_SERVICES.contains(zpoint.getContServiceTypeKeyname())) {
			return HWATER_COLUMNS.stream().sorted().map(i -> {
				return new EntityColumn(i, "NUMERIC");
			}).collect(Collectors.toList());
		}

		return new ArrayList<EntityColumn>();
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
			return new ArrayList<>();
		}

		return contZPointMetadataRepository.selectZOntZPointMetadata(key.contZPointId, key.deviceObjectId);
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

		List<DeviceObject> deviceObjects = contZPointService.selectDeviceObjects(contZPointId);
		if (deviceObjects.isEmpty() || deviceObjects.size() > 1) {
			return null;
		}

		DeviceObject deviceObject = deviceObjects.get(0);

		ContZPointMetadataKey result = new ContZPointMetadataKey();
		result.tsNumber = zpoint.getTsNumber();
		result.contZPoint = zpoint;
		result.deviceObjectId = deviceObject.getId();
		result.deviceObject = deviceObject;

		return result;

	}

}
