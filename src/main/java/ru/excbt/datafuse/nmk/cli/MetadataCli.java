package ru.excbt.datafuse.nmk.cli;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ru.excbt.datafuse.nmk.data.constant.TimeDetailKey;
import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataJson;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.service.DeviceMetadataService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectDataJsonService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectMetaService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.metadata.JsonMetadataParser;
import ru.excbt.datafuse.nmk.metadata.MetadataFieldValue;
import ru.excbt.datafuse.nmk.metadata.MetadataInfo;

public class MetadataCli extends AbstractDBToolCli {

	private static final Logger logger = LoggerFactory
			.getLogger(MetadataCli.class);

	private final static long DEVICE_OBJECT_22 = 22;

	private final static Pageable PAGE_LIMIT_1 = new PageRequest(0, 1);

	@Autowired
	private DeviceObjectMetaService deviceObjectMetaService;

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
		app.readDeviceObjectModel(1);
		// app.readMetaVzlet();
	}

	private void readMetaVzlet() {

		List<DeviceObjectMetaVzlet> metaList = deviceObjectMetaService
				.findMetaVzlet(DEVICE_OBJECT_22);

		checkState(metaList.size() > 0);

		metaList.stream().forEach(
				(val) -> logger.info("VzletTableDay: {}, VzletTableHour: {}",
						val.getVzletTableDay(), val.getVzletTableHour()));

	}

	private void readDeviceObjectModel(int propVar) {
		DeviceObject deviceObject = deviceObjectService
				.findOne(DEVICE_OBJECT_22);

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

		checkState(deviceMetadataList.size() == resultMetadataList.size());

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
			e.printStackTrace();
		}
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
