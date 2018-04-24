package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrPrefValue;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrPref;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class SubscrPrefServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPrefServiceTest.class);

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Autowired
	private SubscrPrefService subscrPrefService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefByType() throws Exception {
		List<SubscrPref> subscrPrefList = subscrPrefService.selectSubscrPrefsBySubscrType("RMA");
		assertNotNull(subscrPrefList);
		assertTrue(subscrPrefList.size() > 0);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscriberPrefValue() throws Exception {

		List<SubscrPrefValue> values = subscrPrefService.selectSubscrPrefValue(portalUserIdsService.getCurrentIds());
		assertTrue(values.size() > 0);
		for (SubscrPrefValue subscrPrefValue : values) {
			logger.info("SubscrPref: {}", subscrPrefValue.getSubscrPrefKeyname());
		}
	}

	/**
	 *
	 * @return
	 */
	public long getSubscriberId() {
		return TestExcbtRmaIds.EXCBT_SUBSCRIBER_ID;
	}

	/**
	 *
	 * @return
	 */
	public long getSubscrUserId() {
		return TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID;
	}

}
