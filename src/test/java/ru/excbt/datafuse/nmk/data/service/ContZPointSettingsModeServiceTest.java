package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;

public class ContZPointSettingsModeServiceTest extends JpaSupportTest {

	public static final long TEST_ZPOINT_ID = 18811570; 
	
	@Autowired
	private ContZPointSettingModeService service;
	
	@Test
	public void test() {
		service.initContZPointSettingMode(TEST_ZPOINT_ID);
	}
	
	@Test
	public void testSave() {
		List<ContZPointSettingMode> modes = service.findSettingByContZPointId(TEST_ZPOINT_ID);
		
		for (ContZPointSettingMode mode : modes) {
			checkNotNull(mode);
			mode.setWm_P1_min(mode.getWm_P1_min() + 0.1);
			service.save(mode);
		}
	}

}
