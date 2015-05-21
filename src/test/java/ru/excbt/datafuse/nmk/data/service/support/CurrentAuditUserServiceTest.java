package ru.excbt.datafuse.nmk.data.service.support;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.AuditUser;

public class CurrentAuditUserServiceTest extends JpaSupportTest {

	@Autowired
	private CurrentUserService currentAuditUserService;
	
	@Test
	public void testCurrentAuditUser() {
		AuditUser au = currentAuditUserService.getCurrentAuditUser();
		assertNotNull(au);
	}
}
