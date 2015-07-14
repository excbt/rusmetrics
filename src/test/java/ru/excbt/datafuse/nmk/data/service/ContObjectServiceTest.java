package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class ContObjectServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectServiceTest.class);

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Test
	public void testIzhevskCont() {
		List<ContObject> res = contObjectService.findContObjectsByFullName("%Ижевск%");
		// assertTrue(res.size() > 0);
		assertNotNull(res);
		logger.info("Found {} ContObjects from Izhevsk", res.size());
	}



}
