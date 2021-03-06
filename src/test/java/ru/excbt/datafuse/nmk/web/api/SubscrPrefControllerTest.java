package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrPrefValue;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

@Transactional
public class SubscrPrefControllerTest extends RmaControllerTest {

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefValuesGet() throws Exception {
		_testGetJson("/api/subscr/subscrPrefValues");
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefValueGet() throws Exception {
		RequestExtraInitializer param = (builder) -> {
			builder.param("subscrPrefKeyname", "SUBSCR_OBJECT_TREE_CONT_OBJECTS");
		};
		_testGetJson("/api/subscr/subscrPrefValue", param);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefValuesSave() throws Exception {
		String prefValuesContent = _testGetJson("/api/subscr/subscrPrefValues");

		List<SubscrPrefValue> prefValues = TestUtils.fromJSON(new TypeReference<List<SubscrPrefValue>>() {
		}, prefValuesContent);

		assertNotNull(prefValues);

		for (SubscrPrefValue v : prefValues) {
			v.setValue("value_" + System.currentTimeMillis());
			v.setSubscrPref(null);
			v.setDevComment(EDITED_BY_REST);
		}

		_testUpdateJson("/api/subscr/subscrPrefValues", prefValues);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefObjectTreeType() throws Exception {
		RequestExtraInitializer param = (builder) -> {
			builder.param("subscrPrefKeyname", "SUBSCR_OBJECT_TREE_CONT_OBJECTS");
		};
		_testGet("/api/subscr/subscrPrefValues/objectTreeTypes", param);
	}

}
