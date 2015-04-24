package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkArgument;
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

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.model.UDirectory;
import ru.excbt.datafuse.nmk.data.service.SubscrActionService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;

@Controller
@RequestMapping("/api/subscr/subscrAction")
public class SubscrActionController extends WebApiController {

	private static final Logger logger = LoggerFactory
			.getLogger(	SubscrActionController.class);
	
	@Autowired
	private SubscrActionService subscrActionService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/groups", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrActionGroup() {
		List<SubscrActionGroup> resultList = subscrActionService
				.findActionGroup(currentSubscriberService.getSubscriberId());
		return ResponseEntity.ok(resultList);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/groups/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneGroup(@PathVariable("id") long id) {
		return ResponseEntity.ok(subscrActionService.findActionGroupOne(id));
	}
	
	/**
	 * 
	 * @param id
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/groups/{id}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneGroup(@PathVariable("id") long id,
			@RequestBody SubscrActionGroup entity) {

		checkNotNull(entity);
		checkNotNull(entity.getId());
		checkArgument(entity.getId().longValue() == id);

		entity.setSubscriber(currentSubscriberService.getSubscriber());
		
		try {
			subscrActionService.updateOneGroup(entity);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during save entity SubscrActionGroup (id={}): {}", id,
					e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}
		return ResponseEntity.accepted().build();
	}	
	
	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSubscrActionUser() {
		List<SubscrActionUser> resultList = subscrActionService
				.findActionUser(currentSubscriberService.getSubscriberId());
		return ResponseEntity.ok(resultList);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getOneUser(@PathVariable("id") long id) {
		return ResponseEntity.ok(subscrActionService.findActionUserOne(id));
	}
	
	/**
	 * 
	 * @param id
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneUser(@PathVariable("id") long id,
			@RequestBody SubscrActionUser entity) {

		checkNotNull(entity);
		checkNotNull(entity.getId());
		checkArgument(entity.getId().longValue() == id);

		entity.setSubscriber(currentSubscriberService.getSubscriber());
		
		try {
			subscrActionService.updateOneUser(entity);
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (TransactionSystemException | PersistenceException e) {
			logger.error("Error during save entity SubscrActionUser (id={}): {}", id,
					e);
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
					.build();
		}
		return ResponseEntity.accepted().build();
	}	
	
}
