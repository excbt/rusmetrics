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
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class LoadingBtsDataTest extends RmaControllerTest {

	private final long BTS_DATA_SOURCE_ID = 128647056;
	private final long DEVICE_MODEL_ID = 128647057;
	private final long RSO_ORGANIZARION_ID = 25201856;
	
	/**
	 * @throws Exception 
	 * 
	 */
	@Test
	@Transactional
	public void testLoadingBtsData() throws Exception {
		List<LoadingBtsData.BtsInfo> btsInfos = LoadingBtsData.loadBtsInfo("БТСки.csv");
		
		for (LoadingBtsData.BtsInfo info : btsInfos) {
			
			ContObject co = new ContObject();
			co.setDescription("Рамашкино БТС");
			co.setName("Ромашкино " + info.getRiserNr() + " БТС № " + info.getBtsNr());
			co.setOwner("РОМАШКИНО");
			co.setTimezoneDefKeyname(TimezoneDefKey.MSK.getKeyname());
			
			// Make ContObject
			Long contObjectId = _testCreateJson("/api/rma/contObjects", co);
			assertNotNull(contObjectId);
			
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
				
				Long deviceObjectId = _testCreateJson(String.format("/api/rma/contObjects/%d/deviceObjects",contObjectId), deviceObject);
				assertNotNull(deviceObjectId);
				deviceObjectIds[i-1] = deviceObjectId;				
				///

			}
			
			// Make ContZPoint
			for (int i = 1; i < 9; i++) {
				ContZPoint contZPoint = new ContZPoint();
				contZPoint.set_activeDeviceObjectId(deviceObjectIds[i-1]);
				contZPoint.setContServiceTypeKeyname(ContServiceTypeKey.CW.getKeyname());
				contZPoint.setContZPointComment("Ромашкино. " + info.getRiserNr() + " БТС № " + info.getBtsNr()+ " (" + info.getAptNr()+ ")");
				contZPoint.setCustomServiceName("Водоснабжение: Имульсный Счетчик");
				contZPoint.setDoublePipe(false);
				contZPoint.setStartDate(LocalDateUtils.asDate(LocalDate.now()));
				contZPoint.setRsoId(RSO_ORGANIZARION_ID);
				Long contZpointId = _testCreateJson(String.format("/api/rma/contObjects/%d/zpoints",contObjectId), contZPoint);
				assertNotNull(contZpointId);
			}	
			
			// Break for cycle of BtsInfo
			break;			
			
		}
		
		
	}
}
