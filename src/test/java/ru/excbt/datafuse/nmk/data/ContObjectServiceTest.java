package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;

public class ContObjectServiceTest extends JpaConfigTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectServiceTest.class);

	@Autowired
	private ContObjectService contObjectService;

	@Test
	public void testIzhevskCont() {
		List<ContObject> res = contObjectService.findByFullName("%Ижевск%");
		assertTrue(res.size() > 0);
		logger.info("Found {} ContObjects from Izhevsk", res.size());
	}

}
