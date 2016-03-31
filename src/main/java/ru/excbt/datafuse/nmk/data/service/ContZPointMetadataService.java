package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
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
import ru.excbt.datafuse.nmk.data.repository.ContZPointMetadataRepository;
import ru.excbt.datafuse.nmk.data.repository.keyname.MeasureUnitRepository;

@Service
public class ContZPointMetadataService {

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

		ContZPoint zpoint = contZPointService.findOne(contZPointId);

		if (zpoint == null) {
			return null;
		}

		Integer metaNumber = zpoint.getTsNumber();

		List<DeviceObject> deviceObjects = contZPointService.selectDeviceObjects(contZPointId);
		if (deviceObjects.isEmpty() || deviceObjects.size() > 1) {
			return result;
		}

		DeviceObject deviceObject = deviceObjects.get(0);

		// select deviceMetadataType from DeviceObjectDataSource 
		List<DeviceObjectDataSource> deviceDataSourceList = deviceObjectDataSourceService
				.selectActiveDeviceObjectDataSource(deviceObject.getId());

		if (deviceDataSourceList.isEmpty() || deviceDataSourceList.size() > 1) {
			return result;
		}

		DeviceObjectDataSource deviceDataSource = deviceDataSourceList.get(0);

		String deviceMetadataType = deviceDataSource.getSubscrDataSource().getDataSourceType().getDeviceMetadataType();

		Long deviceModelId = deviceObject.getDeviceModelId();

		List<DeviceMetadata> deviceMetadataList = deviceMetadataService.selectDeviceMetadata(deviceModelId,
				deviceMetadataType);

		List<DeviceMetadata> transformedMetadataList = deviceMetadataService
				.transformDeviceMetadata(deviceMetadataList);

		List<DeviceMetadata> tsMetadataList = transformedMetadataList.stream()
				.filter(i -> i.getMetaNumber() == null || (metaNumber != null && i.getMetaNumber().equals(metaNumber)))
				.collect(Collectors.toList());

		for (DeviceMetadata m : tsMetadataList) {
			ContZPointMetadata contZPointMetadata = contZPointMetadataFactory(m);
			contZPointMetadata.setContZPoint(zpoint);
			contZPointMetadata.setContZPointId(zpoint.getId());
			contZPointMetadata.setDeviceObjectId(deviceObject.getId());
			result.add(contZPointMetadata);
		}

		return result;
	};

	/**
	 * 
	 * @param src
	 * @return
	 */
	private ContZPointMetadata contZPointMetadataFactory(DeviceMetadata src) {

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

		return dst;
	}

}
