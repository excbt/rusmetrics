package ru.excbt.datafuse.nmk.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SubscriberRepositoryTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscriberRepositoryTest.class);

	@Autowired
	private SubscriberRepository subscrOrgRepository;

	@Test
	public void testSubscrRole() {
		Subscriber sr = subscrOrgRepository.findOne(728L);
		assertNotNull(sr);
		logger.debug("subscrRole ID {}", sr.getId());
		assertNotNull(sr.getOrganization());
		logger.debug("subscrRole Organizatoin ID {}", sr.getOrganization().getId());

		List<Subscriber> soList = subscrOrgRepository.selectByOrganizationId(Long.valueOf(726));
		checkArgument(soList.size() > 0);
	}

}
