package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.RawModemModel;
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
		_testGetJson(apiRmaUrl("/dataSources"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataSourceTypesGet() throws Exception {
		_testGetJson(apiRmaUrl("/dataSourceTypes"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDataSourceCreateUpdateDelete() throws Exception {
		SubscrDataSource dataSource = new SubscrDataSource();
		dataSource.setDataSourceTypeKey(ExSystemKey.DEVICE.getKeyname());
		Long dataSourceId = _testCreateJson(apiRmaUrl("/dataSources"), dataSource);
		assertNotNull(dataSourceId);

		String dataSourceContent = _testGetJson(apiRmaUrl("/dataSources/" + dataSourceId));

		dataSource = fromJSON(new TypeReference<SubscrDataSource>() {
		}, dataSourceContent);

		dataSource.setRawTimeout(10);

		dataSource.setDataSourceComment("DataSource CRUD test at " + System.currentTimeMillis());
		_testUpdateJson(apiRmaUrl("/dataSources/" + dataSource.getId().toString()), dataSource);
		_testDeleteJson(apiRmaUrl("/dataSources/" + dataSourceId.toString()));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRawModelModels() throws Exception {

		String content = _testGetJson("/api/rma/dataSources/rawModemModels");

		List<RawModemModel> result = fromJSON(new TypeReference<List<RawModemModel>>() {
		}, content);

		assertNotNull(result);

	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testCreateModel() throws Exception {
		RawModemModel newModel = new RawModemModel();
		newModel.setRawModemType("GPRS-MODEM");
		newModel.setRawModemModelName("Модель для теста + ");
		newModel.setIsDialup(true);
		newModel.setDevComment("Created by REST");
		Long id = _testCreateJson("/api/rma/dataSources/rawModemModels", newModel);

		String content = _testGetJson("/api/rma/dataSources/rawModemModels/" + id);

		RawModemModel result = fromJSON(new TypeReference<RawModemModel>() {
		}, content);

		assertNotNull(result);

		result.setDevComment("Edited By REST");
		_testUpdateJson("/api/rma/dataSources/rawModemModels/" + id, result);

		_testDeleteJson("/api/rma/dataSources/rawModemModels/" + id);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testModemModelIdentity() throws Exception {
		_testGetJson("/api/rma/dataSources/rawModemModels/rawModemModelIdentity");

	}

}
