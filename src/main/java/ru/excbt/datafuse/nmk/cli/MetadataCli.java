package ru.excbt.datafuse.nmk.cli;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataJson;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.model.VzletSystem;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.service.DeviceMetadataService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectDataJsonService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.metadata.JsonMetadataParser;
import ru.excbt.datafuse.nmk.metadata.MetadataFieldValue;
import ru.excbt.datafuse.nmk.metadata.MetadataInfo;

public class MetadataCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(MetadataCli.class);

	//private final static long[] DEVICE_OBJECTS = { 22, 23, 408, 273, 270, 279,
	//		537 };
	 private final static long[] DEVICE_OBJECTS = { 22 };

	private final static Pageable PAGE_LIMIT_1 = new PageRequest(0, 1);

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private DeviceMetadataService deviceMetadataService;

	@Autowired
	private DeviceObjectDataJsonService deviceObjectDataJsonService;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MetadataCli app = new MetadataCli();
		app.autowireBeans();
		app.showAppStatus();
		boolean result = true;
		for (long id : DEVICE_OBJECTS) {
			result = result & app.readMetaVzlet(id);
			// app.readDeviceObjectModel(id, 1);
		}

		if (result) {
			System.out.println(StringUtils.repeat('=', 100));
			System.out.println("All Objects with ids: "
					+ Arrays.toString(DEVICE_OBJECTS) + " PASSED");
			System.out.println(StringUtils.repeat('=', 100));
		} else {
			System.err.println(StringUtils.repeat('=', 100));
			System.err.println("WE HAVE SOME ERRORS. See app log");
			System.err.println(StringUtils.repeat('=', 100));
		}
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @param vzletSystem
	 */
	private boolean checkSystem(long deviceObjectId, VzletSystem vzletSystem) {
		checkNotNull(vzletSystem);
		if (vzletSystem.getId() == 0) {
			return true;
		}
		logger.info("deviceObjectId:{} VzletSystem1: id={}; service_type:{}",
				deviceObjectId, vzletSystem.getId(),
				vzletSystem.getContServiceTypeKey());

		return readDeviceObjectModel(deviceObjectId, vzletSystem.getId()
				.intValue());
		//
	}

	/**
	 * 
	 * @param deviceObjectId
	 */
	private boolean readMetaVzlet(long deviceObjectId) {

		boolean result = true;

		List<DeviceObjectMetaVzlet> metaList = deviceObjectService
				.findDeviceObjectMetaVzlet(deviceObjectId);

		checkState(metaList.size() > 0);

		for (DeviceObjectMetaVzlet meta : metaList) {

			logger.info("VzletTableDay: {}, VzletTableHour: {}",
					meta.getVzletTableDay(), meta.getVzletTableHour());

			if (meta.getVzletSystem1() != null) {
				result = result
						& checkSystem(deviceObjectId, meta.getVzletSystem1());
			}
			if (meta.getVzletSystem2() != null) {
				result = result
						& checkSystem(deviceObjectId, meta.getVzletSystem2());
			}
			if (meta.getVzletSystem3() != null) {
				result = result
						& checkSystem(deviceObjectId, meta.getVzletSystem3());
			}

		}

		return result;
	}

	private boolean readDeviceObjectModel(long deviceObjectId, int propVar) {
		boolean result = true;

		DeviceObject deviceObject = deviceObjectService.findOne(deviceObjectId);

		DeviceModel model = deviceObject.getDeviceModel();

		logger.info("DeviceObjectModel: {}. ExSystem:{}. ExLabel:{} ",
				model.getModelName(), model.getExSystem(), model.getExLabel());

		logger.info("deviceModelId: {}", model.getId());

		List<DeviceMetadata> deviceMetadataList = deviceMetadataService
				.findDeviceMetadata(model.getId());

		List<MetadataInfo> resultMetadataList = JsonMetadataParser
				.processPropsVars(
						Collections.unmodifiableList(deviceMetadataList),
						propVar);

		// checkState(deviceMetadataList.size() == resultMetadataList.size());

		DeviceObjectDataJson jsonData = readJsonData(deviceObject.getId(),
				LocalDateTime.of(2011, 10, 1, 0, 0));
		checkNotNull(jsonData);

		logger.info(jsonData.getDataJson());
		try {
			List<MetadataFieldValue> fieldValues = JsonMetadataParser
					.processJsonFieldValues(jsonData.getDataJson(),
							resultMetadataList);
			fieldValues.stream().forEach((val) -> logger.info("{}", val));

			checkState(resultMetadataList.size() == fieldValues.size());

		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param deviceObjectId
	 * @return
	 */
	private DeviceObjectDataJson readJsonData(Long deviceObjectId,
			LocalDateTime localDate) {
		checkNotNull(localDate);
		logger.info("Date: {}", localDate);

		List<DeviceObjectDataJson> jsonList = deviceObjectDataJsonService
				.selectDeviceObjectDataJson(deviceObjectId,
						TimeDetailKey.TYPE_24H, localDate, PAGE_LIMIT_1);

		checkState(!jsonList.isEmpty());

		DeviceObjectDataJson jsonData = jsonList.get(0);
		return jsonData;
	}

}
