package ru.excbt.datafuse.nmk.data.service.support;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.excbt.datafuse.nmk.security.PasswordUtils;

public class PasswordUtilsTest {

	private static final Logger logger = LoggerFactory.getLogger(PasswordUtilsTest.class);

	@Test
	public void testGeneratePassword() throws Exception {

		logger.info("new generated password {}", PasswordUtils.generateRandomPassword());

	}

}
