package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ru.excbt.datafuse.nmk.data.model.SubscrSessionTask;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaSubscrSessionTaskControllerTest extends RmaControllerTest {

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateSubscrSessionTask() throws Exception {

		SubscrSessionTask task = new SubscrSessionTask();
		task.setDeviceObjectId(719L);
		task.setSessionDetailTypes(new String[] { TimeDetailKey.TYPE_1DAY.getKeyname() });
		//task.setContZpointId(contZpointId);
		Long id = _testCreateJson("/api/rma/subscrSessionTask", task);
		assertNotNull(id);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSubscrSessionTask() throws Exception {
		_testGetJson("/api/rma/subscrSessionTask/" + 127990560);
	}

	@Test
	public void testGetSubscrSessionTaskSession() throws Exception {
		_testGetJson("/api/rma/subscrSessionTask/" + 127990560 + "/logSessions");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetContZPointSessionDetailType() throws Exception {
		_testGetJson("/api/rma/subscrSessionTask/contZPointSessionDetailType/byDeviceObject/" + 719);
	}

}
