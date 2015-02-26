package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/META-INF/spring/jpa-config.xml")
public class JpaConfigTest {

	@Autowired
	protected EntityManager entityManager;
	
	
	@Test
	public void entityManagerTest() {
		assertNotNull(entityManager);
	}
	

}
