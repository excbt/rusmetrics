package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriodParser;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;

public class ContZPointServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(ContZPointServiceTest.class);

	private final static long MANUAL_CONT_OBJECT_ID = 60695605;

	@Autowired
	private ContZPointService contZPointService;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private DeviceModelService deviceModelService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateManualZPoint() throws Exception {
		ContZPoint contZPoint = contZPointService.createManualZPoint(MANUAL_CONT_OBJECT_ID, ContServiceTypeKey.HEAT,
				LocalDate.now(), 1, null, null);
		assertNotNull(contZPoint);
		logger.info("Created ZPoint:{}", contZPoint.getId());
		contZPointService.deleteManualZPoint(contZPoint.getId());
	}

	/**
	 * 
	 * @param contZPointId
	 * @param contServiceTypeKey
	 * @param tsNumber
	 * @throws Exception
	 */
	private DeviceObject addCustomZPoint(Long contObjectId, ContServiceTypeKey contServiceTypeKey, int tsNumber,
			Boolean isDoublePipe, Long deviceModelId, String deviceNumber) throws Exception {

		DeviceModel deviceModel = deviceModelService.findOne(deviceModelId);

		DeviceObject deviceObject = new DeviceObject();
		deviceObject.setDeviceModel(deviceModel);
		deviceObject.setNumber(deviceNumber);
		deviceObjectService.createManualDeviceObject(deviceObject);

		checkNotNull(deviceObject.getId());

		addCustomZPoint(contObjectId, contServiceTypeKey, tsNumber, isDoublePipe, deviceObject);

		return deviceObject;
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contServiceTypeKeys
	 * @param tsNumber
	 * @param deviceObject
	 * @throws Exception
	 */
	private void addCustomZPoint(Long contObjectId, ContServiceTypeKey contServiceTypeKey, int tsNumber,
			Boolean isDoublePipe, DeviceObject deviceObject) throws Exception {

		LocalDate localDate = LocalDate.parse("2015-01-01", LocalDatePeriodParser.DATE_FORMATTER);

		contZPointService.createManualZPoint(contObjectId, contServiceTypeKey, localDate, tsNumber, isDoublePipe,
				deviceObject);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testObj() throws Exception {
		Long contObjectId = Long.valueOf(60696286);
		Long deviceModelId = Long.valueOf(29779958);
		String deviceNumber = "00219936";

		DeviceObject deviceObject = addCustomZPoint(contObjectId, ContServiceTypeKey.HEAT, 1, null, deviceModelId,
				deviceNumber);

		// addCustomZPoint(contObjectId,
		// ContServiceTypeKey.HW, 2, true, deviceObject);
	}

	@Test
	@Ignore
	public void testDelTemp() throws Exception {
		contZPointService.deleteOnePermanent(66183371L);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetDeviceObjectIds() throws Exception {
		List<Long> deviceObjects = contZPointService.selectDeviceObjectIds(159919982);
		assertFalse(deviceObjects.isEmpty());
		logger.info("deviceObjectId:{}", deviceObjects.get(0));
	}

}
