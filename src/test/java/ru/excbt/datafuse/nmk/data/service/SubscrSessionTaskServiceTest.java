package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.SubscrSessionTask;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SubscrSessionTaskServiceTest extends PortalDataTest {

	@Autowired
	private SubscrSessionTaskService subscrSessionTaskService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrSessionTaskService() throws Exception {
		SubscrSessionTask entity = new SubscrSessionTask();
		entity.setTaskCreateDate(new Date());
		String[] timeDetailTypes = new String[] { TimeDetailKey.TYPE_1DAY.getKeyname(),
				TimeDetailKey.TYPE_1H.getKeyname(), TimeDetailKey.TYPE_1MON.getKeyname() };

		entity.setSessionDetailTypes(timeDetailTypes);
		SubscrSessionTask result = subscrSessionTaskService.saveSubscrSessionTask(entity);
		assertNotNull(result);

	}
}
