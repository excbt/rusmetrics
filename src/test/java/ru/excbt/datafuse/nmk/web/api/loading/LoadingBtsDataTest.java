/**
 *
 */
package ru.excbt.datafuse.nmk.web.api.loading;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointFullVM;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.model.dto.EditDataSourceDTO;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimezoneDefKey;
import ru.excbt.datafuse.nmk.utils.LoadingBtsData;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;
import ru.excbt.datafuse.nmk.utils.ResourceHelper;
import ru.excbt.datafuse.nmk.utils.LoadingBtsData.BtsInfo;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.*;
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

	/**
	 *
	 */

	private static final Logger log = LoggerFactory.getLogger(LoadingBtsDataTest.class);

	private final long BTS_DATA_SOURCE_ID = 128908069;
	private final long DEVICE_MODEL_ID = 128647057;
	private final long RSO_ORGANIZARION_ID = 66244571;
	private final long CM_ORGANIZARION_ID = 31904329;
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

		List<LoadingBtsData.BtsInfo> btsInfos = LoadingBtsData.loadBtsInfo("./bts-loading/БТСки.csv");

		int cnt = 0;

		for (LoadingBtsData.BtsInfo info : btsInfos) {

			LoadingResult res = new LoadingResult();
			res.info = info;

			ContObjectDTO contObjectDTO = new ContObjectDTO();
			contObjectDTO.setFullName("Ромашково, корпус 1, " + info.getRiserNr().toLowerCase() + ", БТС " + info.getBtsNr());
			contObjectDTO.setDescription("Ромашкино БТС");
			contObjectDTO.setFullName("Ромашково, корпус 1, " + info.getRiserNr().toLowerCase() + ", БТС " + info.getBtsNr());
			contObjectDTO.setOwner("РОМАШКОВО");
			contObjectDTO.setTimezoneDefKeyname(TimezoneDefKey.MSK.getKeyname());
			contObjectDTO.setCurrentSettingMode("summer");

			RequestExtraInitializer params = (builder) -> {
				builder.param("cmOrganizationId", String.valueOf(CM_ORGANIZARION_ID));
			};

			// Make ContObject
			Long contObjectId = _testCreateJson("/api/rma/contObjects", contObjectDTO, params);
			assertNotNull(contObjectId);
			res.contObjectId = contObjectId;

			// Make DeviceObject

			Long[] deviceObjectIds = new Long[8];

			for (int i = 1; i < 9; i++) {
				DeviceObjectDTO deviceObjectDTO = new DeviceObjectDTO();
				deviceObjectDTO.setDeviceModelId(DEVICE_MODEL_ID);
				deviceObjectDTO.setIsImpulse(true);
				deviceObjectDTO.setImpulseK(0.01);
				deviceObjectDTO.setImpulseMu("V_M3");
				deviceObjectDTO.setImpulseCounterAddr(info.getBtsNr());
				deviceObjectDTO.setImpulseCounterSlotAddr(String.valueOf(i));
				deviceObjectDTO.setImpulseCounterType("BTS_2");
				deviceObjectDTO.setEditDataSourceInfo(new EditDataSourceDTO());
				deviceObjectDTO.getEditDataSourceInfo().setSubscrDataSourceId(BTS_DATA_SOURCE_ID);
				deviceObjectDTO.setNumber(info.getBtsNr() + "#" + i);

				Long deviceObjectId = _testCreateJson(
						String.format("/api/rma/contObjects/%d/deviceObjects", contObjectId), deviceObjectDTO);
				assertNotNull(deviceObjectId);
				deviceObjectIds[i - 1] = deviceObjectId;
				res.deviceObjectIds.add(deviceObjectId);
				///

			}

			// Make ContZPoint
			for (int i = 1; i < 9; i++) {
				ContZPointFullVM contZPointVM = new ContZPointFullVM();
				contZPointVM.setDeviceObjectId(deviceObjectIds[i - 1]);
				contZPointVM.setContServiceTypeKeyname(ContServiceTypeKey.CW.getKeyname());
				contZPointVM.setContZPointComment(
						"Ромашково, корпус 1, " + info.getRiserNr().toLowerCase() + ", БТС " + info.getBtsNr() + " (" + info.getAptNr().toLowerCase() + ")");
				contZPointVM.setCustomServiceName("Водоснабжение: Имульсный Счетчик, "+ " (" + info.getAptNr().toLowerCase() + ")");
				contZPointVM.setDoublePipe(false);
				contZPointVM.setStartDate(LocalDateUtils.asDate(LocalDate.now()));
				contZPointVM.setRsoId(RSO_ORGANIZARION_ID);
                //contZPoint.setDeviceObject();
				Long contZpointId = _testCreateJson(String.format("/api/rma/contObjects/%d/zpoints", contObjectId),
						contZPointVM);
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
			doDelete (loadingResults);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	@Transactional
	public void testDeleteBtsData() throws Exception {

		List<LoadingResult> loadingResults = new ArrayList<>();

		File f = ResourceHelper.findResource("./bts-loading/result-bts-test.txt");
		log.info("File: {}", f.getAbsolutePath());
		try (Reader r = new FileReader("result-ts.txt")) {
			try (BufferedReader br = new BufferedReader(r)) {
				String line;
				Long contObjectId = 0L;
				Long contZpointId = 0L;
				Long deviceObjectId = 0L;
				LoadingResult lr = null;
				while ((line = br.readLine()) != null) {
					log.debug("Line: {}", line);
					String data[] = line.split(":");
					String type = data[0].trim();
					log.debug("type: {}, ID: {}", type, data[1]);
					if (type.equals("A")) {
						contObjectId = Long.parseLong(data[1]);
						if (lr != null) {
							loadingResults.add(lr);
						}
						lr = new LoadingResult();
						lr.contObjectId = contObjectId;
					}
					if (type.equals("B") && lr != null) {
						contZpointId = Long.parseLong(data[1]);
						lr.contZPointIds.add(contZpointId);
					}
					if (type.equals("C") && lr != null) {
						deviceObjectId = Long.parseLong(data[1]);
						lr.deviceObjectIds.add(deviceObjectId);
					}
				}

				if (lr != null && !loadingResults.contains(lr)) {
					loadingResults.add(lr);
				}
			}
		}
		doDelete (loadingResults);
	}


	/**
	 *
	 */
	private void doDelete(List<LoadingResult> loadingResults) throws Exception {
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
