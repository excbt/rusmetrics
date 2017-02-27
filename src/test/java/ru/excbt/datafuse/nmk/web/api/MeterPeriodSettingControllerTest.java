/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api;

import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.service.MeterPeriodSettingService;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for MeterPeriodSettingController
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.02.2017
 * 
 */
public class MeterPeriodSettingControllerTest extends RmaControllerTest {

	@Autowired
	private MeterPeriodSettingService meterPeriodSettingService;

	@Test
	@Transactional
	public void testCreate() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
		Long id = _testCreateJson("/api/subscr/meter-period-settings", setting);
		assertNotNull(id);
	}

	@Test
	@Transactional
	public void testGetOne() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
		setting = meterPeriodSettingService.save(setting);
		String content = _testGetJson("/api/subscr/meter-period-settings/" + setting.getId());
		MeterPeriodSettingDTO result = fromJSON(new TypeReference<MeterPeriodSettingDTO>() {
		}, content);
		assertEquals(setting.getId(), result.getId());
	}

	@Test
	@Transactional
	public void testGet() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
		setting = meterPeriodSettingService.save(setting);
		final MeterPeriodSettingDTO checkSetting = new MeterPeriodSettingDTO(setting);
		_testGetJsonResultActions("/api/subscr/meter-period-settings").andDo((result) -> {
			List<MeterPeriodSettingDTO> resultDTOs = fromJSON(new TypeReference<List<MeterPeriodSettingDTO>>() {
			}, result.getResponse().getContentAsString());
			assertTrue(resultDTOs.stream().filter(i -> i.getId().equals(checkSetting.getId())).findAny().isPresent());

		});
	}

	@Test
	@Transactional
	public void testUpdate() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
		setting = meterPeriodSettingService.save(setting);
		MeterPeriodSettingDTO newSetting = new MeterPeriodSettingDTO(setting);
		newSetting.setName("New Name");
		_testPutJson("/api/subscr/meter-period-settings", newSetting).andExpect(status().isOk()).andDo((result) -> {
			MeterPeriodSettingDTO resultDTO = fromJSON(new TypeReference<MeterPeriodSettingDTO>() {
			}, result.getResponse().getContentAsString());
			assertEquals(resultDTO.getId(), newSetting.getId());
			assertEquals(resultDTO.getName(), newSetting.getName());
		});
	}

	@Test
	@Transactional
	public void testDelete() throws Exception {
		MeterPeriodSettingDTO setting = MeterPeriodSettingDTO.builder().name("MySetting").build();
		setting = meterPeriodSettingService.save(setting);
		_testDeleteJson("/api/subscr/meter-period-settings/" + setting.getId());
	}

}
