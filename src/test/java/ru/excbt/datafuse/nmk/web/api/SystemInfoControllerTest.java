package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.dto.ExSystemDto;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

@Transactional
public class SystemInfoControllerTest extends AnyControllerTest {

	@Test
//    @Ignore
	public void testFullUserInfo() throws Exception {
		_testGetJson("/api/systemInfo/fullUserInfo");
	}

	@Test
	public void testReadOnlyMode() throws Exception {
		_testGetJson("/api/systemInfo/readOnlyMode");
	}

	@Test
    @Ignore
	public void testChangePassword() throws Exception {

		String urlStr = "/api/systemInfo/passwordChange";

		RequestExtraInitializer requestExtraInitializer = (builder) -> {
			builder.contentType(MediaType.APPLICATION_JSON).param("oldPassword", "admin").param("newPassword",
					"admin1");
		};

		_testPutJson(urlStr, requestExtraInitializer);

		RequestExtraInitializer requestExtraInitializerBack = (builder) -> {
			builder.contentType(MediaType.APPLICATION_JSON).param("oldPassword", "admin1").param("newPassword",
					"admin");
		};

		_testPutJson(urlStr, requestExtraInitializerBack);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testExSystem() throws Exception {
		String content = _testGetJson("/api/systemInfo/exSystem");
		List<ExSystemDto> result = TestUtils.fromJSON(new TypeReference<List<ExSystemDto>>() {
		}, content);
		assertNotNull(result);
	}
}
