package ru.excbt.datafuse.nmk.data.service.support;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.support.CurrentUserService;

public class CurrentUserServiceTest extends JpaSupportTest {

	@Autowired
	private CurrentUserService currentUserService;
	
	@Test
	public void testCurrentUser() {
		AuditUser au = currentUserService.getCurrentAuditUser();
		//assertNotNull(au);
	}
}
