package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPointSettingMode;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@RunWith(SpringRunner.class)
public class ContZPointSettingsModeServiceTest extends PortalDataTest {

	public static final long TEST_ZPOINT_ID = 18811570;

	@Autowired
	private ContZPointSettingModeService service;

	@Test
    @Transactional
	public void test() {
		service.initContZPointSettingMode(TEST_ZPOINT_ID);
	}

	@Test
    @Transactional
	public void testSave() {
		List<ContZPointSettingMode> modes = service.findSettingByContZPointId(TEST_ZPOINT_ID);

		for (ContZPointSettingMode mode : modes) {
			checkNotNull(mode);
			mode.setWm_P1_min(mode.getWm_P1_min() + 0.1);
			service.save(mode);
		}
	}

}
