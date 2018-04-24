package ru.excbt.datafuse.nmk.data.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SubscrObjectTreeContObjectServiceTest extends PortalDataTest {

	@Autowired
	private SubscrObjectTreeContObjectService subscrObjectTreeContObjectService;

//	@Autowired
//	private SubscrContObjectService subscrContObjectService;

    @Autowired
	private ObjectAccessService objectAccessService;

	/**
	 * Test ignored due to deleting of subscrObjectTree ID = 64166466L
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testGetContObjectIds() throws Exception {

		SubscriberParam param = SubscriberParam.builder().subscriberId(64166466L).build();

		List<Long> contObjectIds = subscrObjectTreeContObjectService.selectTreeContObjectIdsAllLevels(param,
				512111663L);
		assertNotNull(contObjectIds);

		List<ContObject> contObjects = objectAccessService.findContObjectsExcludingIds(64166466L,
            contObjectIds);

		assertNotNull(contObjects);

	}
}
