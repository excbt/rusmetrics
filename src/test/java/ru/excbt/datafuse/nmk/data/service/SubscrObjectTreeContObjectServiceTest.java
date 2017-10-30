package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class SubscrObjectTreeContObjectServiceTest extends JpaSupportTest {

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
