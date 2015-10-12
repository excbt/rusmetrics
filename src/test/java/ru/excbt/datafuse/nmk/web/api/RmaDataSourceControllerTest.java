package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.ManualControllerTest;

public class RmaDataSourceControllerTest extends ManualControllerTest {

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	private SubscrDataSourceService subscrDataSourceService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataSourcesGet() throws Exception {
		_testJsonGet(apiRmaUrl("/dataSources"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataSourceTypesGet() throws Exception {
		_testJsonGet(apiRmaUrl("/dataSourceTypes"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataSourceCreateUpdateDelete() throws Exception {
		SubscrDataSource dataSource = new SubscrDataSource();
		dataSource.setDataSourceTypeKey(ExSystemKey.DEVICE.getKeyname());
		Long dataSourceId = _testJsonCreate(apiRmaUrl("/dataSources"), dataSource);
		assertNotNull(dataSourceId);

		dataSource = subscrDataSourceService.findOne(dataSourceId);
		dataSource.setDataSourceComment("DataSource CRUD test at " + System.currentTimeMillis());
		_testJsonUpdate(apiRmaUrl("/dataSources/" + dataSource.getId().toString()), dataSource);
		_testJsonDelete(apiRmaUrl("/dataSources/" + dataSourceId.toString()));
	}

}
