package ru.excbt.datafuse.nmk.data.repository;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrOrg;
import ru.excbt.datafuse.nmk.data.repository.SubscrOrgRepository;

public class SubscrOrgRepositoryTest extends JpaSupportTest {

	
	private static final Logger logger = LoggerFactory
			.getLogger(SubscrOrgRepositoryTest.class);
	
	@Autowired
	private SubscrOrgRepository subscrOrgRepository;

	
	@Test
	public void testSubscrOrg() {
		SubscrOrg so = subscrOrgRepository.findOne(728L);
		assertNotNull(so);
		logger.debug("subscrObj ID {}", so.getId());
		assertNotNull(so.getOrganization());
		logger.debug("subscrObj Organizatoin ID {}", so.getOrganization().getId());
		
		
		List<SubscrOrg> soList = subscrOrgRepository.selectByOrganizationId(726);
		checkArgument(soList.size() > 0);
		logger.debug("subscrObj List Organizatoin FullName {}", soList.get(0).getOrganization().getFullName());
	}

}
