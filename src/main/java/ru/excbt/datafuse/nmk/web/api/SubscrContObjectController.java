package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping(value = "/api/subscr")
public class SubscrContObjectController extends WebApiController {

	// private final static int TEST_SUBSCRIBER_ID = 728;

	private static final Logger logger = LoggerFactory
			.getLogger(SubscrContObjectController.class);

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private SubscriberService subscrUserService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@RequestMapping(value = "/contObjects", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getContObjectsList() {
		logger.debug("Fire listAll");

		List<ContObject> resultList = subscrUserService
				.selectSubscriberContObjects(currentSubscriberService
						.getSubscriberId());

		return ResponseEntity.ok().body(resultList);
	}

	/**
	 * 
	 * @param contObjectId
	 * @param contObject
	 * @return
	 */
	@RequestMapping(value = "/contObjects/{contObjectId}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateContObject(
			@PathVariable("contObjectId") Long contObjectId,
			@RequestBody ContObject entity) {

		checkNotNull(contObjectId);
		checkNotNull(entity);

		if (entity.isNew()) {
			return ResponseEntity.badRequest().build();
		}

		ContObject resultEntity = null;

		try {
			resultEntity = contObjectService.updateOne(entity);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during update ContObject (id={}): {}",
					entity.getId(), e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}

		return ResponseEntity.accepted().body(resultEntity);
	}

}
