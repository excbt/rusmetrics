package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class RmaContObjectControllerTest extends AnyControllerTest {

	@Autowired
	private ContObjectService contObjectService;

	@Test
	public void testContObjectGet() throws Exception {
		_testJsonGet(apiRmaUrl("/contObjects"));
	}

	@Test
	public void testContObjectCRUD() throws Exception {
		ContObject contObject = new ContObject();
		contObject.setComment("Created by Test");
		contObject.setTimezoneDefKeyname("MSK");
		contObject.setName("Cont Object TEST");

		Long contObjectId = _testJsonCreate(apiRmaUrl("/contObjects"), contObject);

		_testJsonGet(apiRmaUrl("/contObjects/" + contObjectId));

		contObject = contObjectService.findOne(contObjectId);
		contObject.setCurrentSettingMode("summer");
		_testJsonUpdate(apiRmaUrl("/contObjects/" + contObjectId), contObject);

		_testJsonDelete(apiRmaUrl("/contObjects/" + contObjectId));
	}

}
