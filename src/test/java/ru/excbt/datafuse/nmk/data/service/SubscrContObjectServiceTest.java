package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.ContObjectShortInfo;
import ru.excbt.datafuse.nmk.data.model.support.ContZPointShortInfo;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

import javax.persistence.Tuple;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class })
@Transactional
public class SubscrContObjectServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectServiceTest.class);


    @Autowired
	private ObjectAccessService objectAccessService;

    @Autowired
    private ContObjectService contObjectService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testContZPointInfo() throws Exception {
		List<ContZPointShortInfo> result = objectAccessService.findContZPointShortInfo(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);
		assertTrue(result.size() > 0);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testSelectContObjectSubscriberIdsByRma() throws Exception {
		List<Long> ids = objectAccessService.findSubscriberIdsByRma(64166466L, 29863789L);

		ids.forEach(i -> logger.info("id:{}", i));

	}

	@Test
    @Transactional
	public void testSelectContObjectShortInfo() throws Exception {
		List<ContObjectShortInfo> result = objectAccessService.findContObjects(64166466L)
                .stream().map((i) -> contObjectService.mapToDTO(i).newShortInfo()).collect(Collectors.toList());

//            subscrContObjectService.selectSubscriberContObjectsShortInfo(64166466L);
		result.forEach(i -> logger.info("id:{}", i.getContObjectId()));
	}

	@Test
    @Transactional
	public void testSubscrDeviceObjects() throws Exception {


        logger.info("CurrentSubscriber: {}", getSubscriberParam().getSubscriberId());

//        List<Tuple> resultRows = subscrContObjectRepository
//				.selectSubscrDeviceObjectByNumber(getSubscriberParam().getSubscriberId(), Arrays.asList("104115"));

		List<Tuple> resultRows2 = objectAccessService.findAllContZPointDeviceObjectsEx(getSubscriberId(), Arrays.asList("111214"));

		assertFalse(resultRows2.isEmpty());

		for (Tuple t : resultRows2) {
			logger.info("\nsubscriberId: {}, deviceObjectId:{}", t.get("subscriberId"), t.get("deviceObjectId"));

		}

	}

}
