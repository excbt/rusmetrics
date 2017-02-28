/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.loading;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimezoneDefKey;
import ru.excbt.datafuse.nmk.utils.LoadingBtsData;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.02.2017
 * 
 */
@WithMockUser(username = "rma-77-admin", 
roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
		"RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN" })
public class LoadingBtsDataTest extends RmaControllerTest {

	private final long BTS_DATA_SOURCE_ID = 128908069;
	private final long DEVICE_MODEL_ID = 128647057;
	private final long RSO_ORGANIZARION_ID = 66244571;
	private final boolean DO_DELETE = false;
	
	private final static Long ROM_RMA_SUBSCRIBER_USER_ID = 103926903L;
	private final static Long ROM_RMA_SUBSCRIBER_ID = 103926764L;
	

	private final static class LoadingResult {
		private LoadingBtsData.BtsInfo info;
		private Long contObjectId;
		private final List<Long> deviceObjectIds = new ArrayList<>();
		private final List<Long> contZPointIds = new ArrayList<>();
	}

	/**
	 * @throws Exception
	 * 
	 */
	@Test
	@Transactional
	public void testLoadingBtsData() throws Exception {

		List<LoadingResult> loadingResults = new ArrayList<>();

		List<LoadingBtsData.BtsInfo> btsInfos = LoadingBtsData.loadBtsInfo("БТСки.csv");

		int cnt = 0;

		for (LoadingBtsData.BtsInfo info : btsInfos) {

			LoadingResult res = new LoadingResult();
			res.info = info;

			ContObject co = new ContObject();
			co.setDescription("Рамашкино БТС");
			co.setName("Ромашкино " + info.getRiserNr() + " БТС № " + info.getBtsNr());
			co.setOwner("РОМАШКИНО");
			co.setTimezoneDefKeyname(TimezoneDefKey.MSK.getKeyname());

			// Make ContObject
			Long contObjectId = _testCreateJson("/api/rma/contObjects", co);
			assertNotNull(contObjectId);
			res.contObjectId = contObjectId;

			// Make DeviceObject

			Long[] deviceObjectIds = new Long[8];

			for (int i = 1; i < 9; i++) {
				DeviceObject deviceObject = new DeviceObject();
				deviceObject.setDeviceModelId(DEVICE_MODEL_ID);
				deviceObject.setIsImpulse(true);
				deviceObject.setImpulseK(BigDecimal.valueOf(0.01));
				deviceObject.setImpulseMu("V_M3");
				deviceObject.setImpulseCounterAddr(info.getBtsNr());
				deviceObject.setImpulseCounterSlotAddr(String.valueOf(i));
				deviceObject.setImpulseCounterType("BTS_2");
				deviceObject.getEditDataSourceInfo().setSubscrDataSourceId(BTS_DATA_SOURCE_ID);
				deviceObject.setNumber(info.getBtsNr() + "#" + i);

				Long deviceObjectId = _testCreateJson(
						String.format("/api/rma/contObjects/%d/deviceObjects", contObjectId), deviceObject);
				assertNotNull(deviceObjectId);
				deviceObjectIds[i - 1] = deviceObjectId;
				res.deviceObjectIds.add(deviceObjectId);
				///

			}

			// Make ContZPoint
			for (int i = 1; i < 9; i++) {
				ContZPoint contZPoint = new ContZPoint();
				contZPoint.set_activeDeviceObjectId(deviceObjectIds[i - 1]);
				contZPoint.setContServiceTypeKeyname(ContServiceTypeKey.CW.getKeyname());
				contZPoint.setContZPointComment(
						"Ромашкино. " + info.getRiserNr() + " БТС № " + info.getBtsNr() + " (" + info.getAptNr() + ")");
				contZPoint.setCustomServiceName("Водоснабжение: Имульсный Счетчик");
				contZPoint.setDoublePipe(false);
				contZPoint.setStartDate(LocalDateUtils.asDate(LocalDate.now()));
				contZPoint.setRsoId(RSO_ORGANIZARION_ID);
				Long contZpointId = _testCreateJson(String.format("/api/rma/contObjects/%d/zpoints", contObjectId),
						contZPoint);
				assertNotNull(contZpointId);
				res.contZPointIds.add(contZpointId);
			}

			loadingResults.add(res);
			cnt++;
			// Break for cycle of BtsInfo
			if (cnt > 3)
				break;

		}

		System.err.println("LOADING RESULTS");

		for (LoadingResult r : loadingResults) {
			System.err.println("БТС: " + r.info);
			System.err.println("contObjectId: " + r.contObjectId);
			for (int i = 0; i < r.contZPointIds.size(); i++) {
				System.err.println(
						"ContZPointId: " + r.contZPointIds.get(i) + "; deviceObjectId: " + r.deviceObjectIds.get(i));
			}
		}

		System.err.println("======================================================");

		for (LoadingResult r : loadingResults) {
			System.err.println("A:" + r.contObjectId);
			for (int i = 0; i < r.contZPointIds.size(); i++) {
				System.err.println("	B:" + r.contZPointIds.get(i));
			}
			for (int i = 0; i < r.deviceObjectIds.size(); i++) {
				System.err.println("		C:"+ r.deviceObjectIds.get(i));
			}
		}

		System.err.println("======================================================");

		if (DO_DELETE)
			for (LoadingResult r : loadingResults) {
				// Delete ContZpoints
				for (int i = 0; i < r.contZPointIds.size(); i++) {
					_testDeleteJson(String.format("/api/rma/contObjects/%d/zpoints/%d", r.contObjectId,
							r.contZPointIds.get(i)));
				}
				// Delete DeviceObjects
				for (int i = 0; i < r.deviceObjectIds.size(); i++) {
					_testDeleteJson(String.format("/api/rma/contObjects/%d/deviceObjects/%d", r.contObjectId,
							r.deviceObjectIds.get(i)));
				}
				//
				_testDeleteJson(String.format("/api/rma/contObjects/%d", r.contObjectId));
			}

	}
	
	
	/**
	 * 
	 * @return
	 */
	@Override
	public long getSubscriberId() {
		return ROM_RMA_SUBSCRIBER_ID;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return ROM_RMA_SUBSCRIBER_USER_ID;
	}	
	
}
