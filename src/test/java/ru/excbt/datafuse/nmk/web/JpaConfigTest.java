package ru.excbt.datafuse.nmk.web;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.excbt.datafuse.nmk.data.repository.DeviceDataTypeRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/spring/jpa-config.xml")
public class JpaConfigTest {

	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private DeviceDataTypeRepository deviceDataTypeRepository;
	
	@Test
	public void simpleTest() {
		assertNotNull(entityManager);
	}
	

}
