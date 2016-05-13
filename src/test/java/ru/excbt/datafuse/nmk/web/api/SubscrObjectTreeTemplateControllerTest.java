package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class SubscrObjectTreeTemplateControllerTest extends RmaControllerTest {

	@Test
	public void testRmaSubscrObjectTreeTemplates() throws Exception {
		String content = _testGetJson("/api/subscr/subscrObjectTreeTemplates");

		List<SubscrObjectTreeTemplate> templates = fromJSON(new TypeReference<List<SubscrObjectTreeTemplate>>() {
		}, content);

		for (SubscrObjectTreeTemplate i : templates) {
			_testGetJson(apiSubscrUrlTemplate("/subscrObjectTreeTemplates/%d/items", i.getId()));
		}

	}
}
