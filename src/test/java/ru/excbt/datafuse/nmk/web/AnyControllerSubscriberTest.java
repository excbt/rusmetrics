package ru.excbt.datafuse.nmk.web;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberService;

public class AnyControllerSubscriberTest extends AnyControllerTest {

	@Autowired
	private CurrentSubscriberService cs;

    @Autowired
	private ObjectAccessService objectAccessService;


	protected List<ContObject> selectSubscriberContObjects() {
		return objectAccessService.findContObjects(cs.getSubscriberId());
	}

	protected ContObject getFirstContObject() {
		List<ContObject> contObjects = selectSubscriberContObjects();
		assertTrue(contObjects.size() > 0);
		return contObjects.get(0);
	}

	protected Long getFirstContObjectId() {
		List<Long> contObjectIds = objectAccessService.findContObjectIds(cs.getSubscriberId());
		assertTrue(contObjectIds.size() > 0);
		return contObjectIds.get(0);
	}

}
