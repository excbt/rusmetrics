package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrSessionTask;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;

public class SubscrSessionTaskServiceTest extends JpaSupportTest {

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
