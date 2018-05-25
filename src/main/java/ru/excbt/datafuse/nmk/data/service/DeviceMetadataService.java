package ru.excbt.datafuse.nmk.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;


import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.repository.DeviceMetadataRepository;
import ru.excbt.datafuse.nmk.metadata.JsonMetadataParser;

/**
 * Сервис для работ с метаданными прибора
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.05.2015
 *
 */
@Service
public class DeviceMetadataService {

	private static final Logger logger = LoggerFactory.getLogger(DeviceMetadataService.class);

	public final static String DEVICE_METADATA_TYPE = "DEVICE";

	public final static String PROP_VARS_SPLITTER = ",";

	@Autowired
	private DeviceMetadataRepository deviceMetadataRepository;

	/**
	 *
	 * @param deviceModelId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<DeviceMetadata> selectDeviceMetadata(Long deviceModelId, String deviceMetadataType) {
		return deviceMetadataRepository.selectDeviceMetadata(deviceModelId, deviceMetadataType);
	}

	/**
	 *
	 * @param deviceMetadataList
	 * @return
	 */
	public List<DeviceMetadata> transformDeviceMetadata(List<DeviceMetadata> deviceMetadataList) {

		List<DeviceMetadata> resultList = Lists.newArrayList(deviceMetadataList);
		List<DeviceMetadata> newDeviceMetadataList = new ArrayList<>();
		List<DeviceMetadata> deleteMetadataList = new ArrayList<>();

		for (DeviceMetadata metadata : deviceMetadataList) {

			if (metadata.getPropVars() != null) {
				String[] propVars = metadata.getPropVars().split(PROP_VARS_SPLITTER);
				if (propVars.length > 1) {
					deleteMetadataList.add(metadata);

					for (String propVar : propVars) {

						Integer metaNumber = Integer.valueOf(propVar);

						DeviceMetadata transformedMetadata = deviceMetadataDeepCopy(metadata);
						transformedMetadata.setPropVars(null);
						transformedMetadata.setMetaNumber(metaNumber);

						String srcPropComplete = metadata.getSrcProp();

						Matcher m = JsonMetadataParser.META_VAR_PATTERN.matcher(metadata.getSrcProp());
						if (m.find()) {
							srcPropComplete = m.replaceAll(propVar);
						}

						transformedMetadata.setSrcProp(srcPropComplete);

						newDeviceMetadataList.add(transformedMetadata);

					}

				}

			}
		}

		resultList.removeAll(deleteMetadataList);
		resultList.addAll(newDeviceMetadataList);

		return resultList;
	}

	/**
	 *
	 * @param src
	 * @return
	 */
	public DeviceMetadata deviceMetadataDeepCopy(DeviceMetadata src) {
		DeviceMetadata dst = new DeviceMetadata();

		dst.setDeviceMetadataType(src.getDeviceMetadataType());
		dst.setDeviceModelId(src.getDeviceModelId());
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

		return dst;
	}

	/**
	 *
	 * @param src
	 * @return
	 */
	public List<DeviceMetadata> deviceMetadataDeepCopy(List<DeviceMetadata> src) {
		List<DeviceMetadata> result = new ArrayList<>();
		for (DeviceMetadata m : src) {
			result.add(deviceMetadataDeepCopy(m));
		}
		return result;
	}

}
