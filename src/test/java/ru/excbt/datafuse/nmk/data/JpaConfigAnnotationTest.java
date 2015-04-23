package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.excbt.datafuse.nmk.config.jpa.JpaConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfig.class})
public class JpaConfigAnnotationTest {

	@PersistenceUnit
	protected EntityManagerFactory entityManagerFactory;
	
	
	@Test
	public void entityManagerOK() {
		assertNotNull(entityManagerFactory);
	}
	

}
