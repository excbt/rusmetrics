package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/spring/jpa-config.xml")
public class RepositoryTest {

	
	private static final Logger logger = LoggerFactory
			.getLogger(RepositoryTest.class);
	
	@Autowired
	private ContObjectRepository contObjectRepository;
	
	@Test
	public void test() {
		assertNotNull(contObjectRepository);
		
		List<ContObject> res = contObjectRepository.selectByUserName(725L);
		
		logger.info("Result count {}", res.size());
		logger.info("Full Address {}", res.get(0).getFullAddress());
	}

}
