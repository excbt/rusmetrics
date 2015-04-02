package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;

public class ContZPointSettingsModeServiceTest extends JpaSupportTest {

	private static final long TEST_ZPOINT_ID = 18811570; 
	
	@Autowired
	private ContZPointSettingModeService service;
	
	@Test
	public void test() {
		service.initContZPointSettingMode(TEST_ZPOINT_ID);
	}

}
