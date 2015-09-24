package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.springframework.http.MediaType;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class SystemInfoControllerTest extends AnyControllerTest {

	@Test
	public void testFullUserInfo() throws Exception {
		_testJsonGet("/api/systemInfo/fullUserInfo");
	}

	@Test
	public void testReadOnlyMode() throws Exception {
		_testJsonGet("/api/systemInfo/readOnlyMode");
	}

	@Test
	public void testChangePassword() throws Exception {

		String urlStr = "/api/systemInfo/passwordChange";

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.contentType(MediaType.APPLICATION_JSON)
					.param("oldPassword", "admin")
					.param("newPassword", "admin1");
		};

		_testJsonPut(urlStr, requestExtraInitializer);

		RequestExtraInitializer requestExtraInitializerBack = (builder) -> {
			builder.contentType(MediaType.APPLICATION_JSON)
			.param("oldPassword", "admin1")
			.param("newPassword", "admin");
		};
		
		_testJsonPut(urlStr, requestExtraInitializerBack);
	}
}
