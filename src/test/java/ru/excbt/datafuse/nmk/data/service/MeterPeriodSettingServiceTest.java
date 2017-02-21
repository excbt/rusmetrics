/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.repository.MeterPeriodSettingRepository;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 
 * @author A.Kovtonyuk 
 * @version 1.0
 * @since 20.02.2017
 * 
 */
public class MeterPeriodSettingServiceTest extends JpaSupportTest {

	
	private static final Logger log = LoggerFactory.getLogger(MeterPeriodSettingServiceTest.class);
	
	@Autowired
	private MeterPeriodSettingService meterPeriodSettingService;
	
	@Autowired
	private SubscrContObjectService subscrContObjectService;
	
	@Autowired
	private MeterPeriodSettingRepository meterPeriodSettingRepository;
	
	/**
	 * 
	 * @return
	 */
	protected MeterPeriodSettingDTO createEntity() {
		return new MeterPeriodSettingDTO();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	@Transactional
	@Test
	public void testSave() throws Exception {
		List<Long> ids = subscrContObjectService.selectSubscriberContObjectIds(getSubscriberId());
		assertFalse(ids.isEmpty());
		
		MeterPeriodSettingDTO setting = createEntity();
		log.info("src: {}" ,setting);
		MeterPeriodSettingDTO result = meterPeriodSettingService.save(setting);
		
		log.info("result: {}" ,result);
		assertTrue(result.getId() != null);
	}
}
