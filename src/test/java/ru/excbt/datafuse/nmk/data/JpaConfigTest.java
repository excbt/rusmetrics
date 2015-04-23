package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.excbt.datafuse.nmk.config.jpa.JpaConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfig.class})
public class JpaConfigTest {

	@PersistenceContext
	protected EntityManager entityManager;
	
	
	@Test
	public void entityManagerOK() {
		assertNotNull(entityManager);
	}
	

}
