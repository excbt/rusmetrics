package ru.excbt.datafuse.nmk.data.service.support;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.AuditUser;

public class CurrentAuditUserServiceTest extends JpaSupportTest {

	@Autowired
	private CurrentAuditUserService currentAuditUserService;
	
	@Test
	public void testCurrentAuditUser() {
		AuditUser au = currentAuditUserService.getCurrentAuditUser();
		//assertNotNull(au);
	}
}
