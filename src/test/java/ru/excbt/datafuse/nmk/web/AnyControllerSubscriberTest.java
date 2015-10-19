package ru.excbt.datafuse.nmk.web;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

public class AnyControllerSubscriberTest extends AnyControllerTest {

	@Autowired
	private CurrentSubscriberService cs;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	protected List<ContObject> selectSubscriberContObjects() {
		return subscrContObjectService.selectSubscriberContObjects(cs.getSubscriberId());
	}

	protected ContObject getFirstContObject() {
		List<ContObject> contObjects = selectSubscriberContObjects();
		assertTrue(contObjects.size() > 0);
		return contObjects.get(0);
	}

	protected Long getFirstContObjectId() {
		return checkNotNull(getFirstContObject()).getId();
	}

}
