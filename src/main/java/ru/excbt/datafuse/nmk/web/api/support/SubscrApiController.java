package ru.excbt.datafuse.nmk.web.api.support;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.web.api.WebApiController;

public class SubscrApiController extends WebApiController {

	@Autowired
	protected SubscriberService subscriberService;

	@Autowired
	protected CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	protected boolean canAccessContObject(Long contObjectId) {
		if (contObjectId == null) {
			return false;
		}
		List<Long> contObjectIds = subscriberService
				.selectSubscriberContObjectIds(currentSubscriberService
						.getSubscriberId());
		return contObjectIds.contains(contObjectId);
	}

	/**
	 * 
	 * @return
	 */
	protected long getSubscriberId() {
		return currentSubscriberService.getSubscriberId();
	}
	
}
