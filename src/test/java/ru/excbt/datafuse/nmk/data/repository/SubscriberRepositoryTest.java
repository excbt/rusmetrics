package ru.excbt.datafuse.nmk.data.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

public class SubscriberRepositoryTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscriberRepositoryTest.class);

	@Autowired
	private SubscriberRepository subscrOrgRepository;

	@Test
	public void testSubscrRole() {
		Subscriber sr = subscrOrgRepository.findOne(728L);
		assertNotNull(sr);
		logger.debug("subscrRole ID {}", sr.getId());
		assertNotNull(sr.getOrganizationId());
		logger.debug("subscrRole Organizatoin ID {}", sr.getOrganizationId());

		List<Subscriber> soList = subscrOrgRepository.selectByOrganizationId(Long.valueOf(726));
		checkArgument(soList.size() > 0);
		//		logger.debug("subscrRole List Organizatoin FullName {}",
		//				soList.get(0).getOrganization().getOrganizationFullName());
	}

}
