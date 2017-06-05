package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.service.SubscrActionGroupService;
import ru.excbt.datafuse.nmk.data.service.SubscrActionUserGroupService;
import ru.excbt.datafuse.nmk.data.service.SubscrActionUserService;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с заданиями абонентов
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.04.2015
 *
 */
@Controller
@RequestMapping("/api/subscr/subscrAction")
public class SubscrActionController extends AbstractSubscrApiResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscrActionController.class);

	@Autowired
	private SubscrActionGroupService subscrActionGroupService;

	@Autowired
	private SubscrActionUserService subscrActionUserService;

	@Autowired
	private SubscrActionUserGroupService subscrActionUserGroupService;

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/groups", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> findAllGroups() {
		List<SubscrActionGroup> resultList = subscrActionGroupService.findAll(getCurrentSubscriberId());
		return ResponseEntity.ok(resultList);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/groups/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> findOneGroup(@PathVariable("id") long id) {
		return ResponseEntity.ok(subscrActionGroupService.findOne(id));
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/groups/{id}/users", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> findUsersByGroup(@PathVariable("id") long id) {
		return ResponseEntity.ok(subscrActionUserGroupService.selectUsersByGroup(id));
	}

	/**
	 *
	 * @param id
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/groups/{id}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneGroup(@PathVariable("id") long id,
			@RequestParam(value = "subscrUserIds", required = false) Long[] subscrUserIds,
			@RequestBody SubscrActionGroup entity) {

		checkNotNull(entity);
		checkArgument(!entity.isNew());
		checkArgument(entity.getId().longValue() == id);

		entity.setSubscriber(currentSubscriberService.getSubscriber());

		final Long[] actionIds = subscrUserIds;

		ApiAction action = new AbstractEntityApiAction<SubscrActionGroup>(entity) {

			@Override
			public void process() {
				setResultEntity(subscrActionGroupService.updateOne(entity, actionIds));
			}

		};

		return WebApiHelper.processResponceApiActionUpdate(action);

	}

	/**
	 *
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/groups", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOneGroup(
			@RequestParam(value = "subscrUserIds", required = false) Long[] subscrUserIds,
			@RequestBody SubscrActionGroup entity, HttpServletRequest request) {

		checkNotNull(entity);
		checkArgument(entity.isNew());

		entity.setSubscriber(currentSubscriberService.getSubscriber());

		final Long[] actionIds = subscrUserIds;

		ApiActionLocation action = new ApiActionEntityLocationAdapter<SubscrActionGroup, Long>(entity, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscrActionGroup processAndReturnResult() {
				return subscrActionGroupService.createOne(entity, actionIds);
			}

		};

		return WebApiHelper.processResponceApiActionCreate(action);

	}

	/**
	 *
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/groups/{id}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneGroup(@PathVariable("id") final long id) {
		ApiAction action = new ApiActionAdapter() {
			@Override
			public void process() {
				subscrActionGroupService.deleteOne(id);
			}
		};
		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> findAllUsers() {
		List<SubscrActionUser> resultList = subscrActionUserService.findAll(getCurrentSubscriberId());
		return ResponseEntity.ok(resultList);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> findOneUser(@PathVariable("id") long id) {
		return ResponseEntity.ok(subscrActionUserService.findOne(id));
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/users/{id}/groups", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> findGroupsByUser(@PathVariable("id") long id) {
		return ResponseEntity.ok(subscrActionUserGroupService.selectGroupsByUser(id));
	}

	/**
	 *
	 * @param id
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/users/{id}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateOneUser(@PathVariable("id") long id,
			@RequestParam(value = "subscrGroupIds", required = false) Long[] subscrGroupIds,
			@RequestBody SubscrActionUser entity) {

		checkNotNull(entity);
		checkNotNull(entity.getId());
		checkArgument(entity.getId().longValue() == id);

		entity.setSubscriber(currentSubscriberService.getSubscriber());

		final Long[] actionGroupIds = subscrGroupIds;

		ApiAction action = new AbstractEntityApiAction<SubscrActionUser>(entity) {

			@Override
			public void process() {
				setResultEntity(subscrActionUserService.updateOne(entity, actionGroupIds));
			}

		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 *
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createOneUser(
			@RequestParam(value = "subscrGroupIds", required = false) Long[] subscrGroupIds,
			@RequestBody SubscrActionUser entity, HttpServletRequest request) {

		checkNotNull(entity);
		checkArgument(entity.isNew());

		entity.setSubscriber(currentSubscriberService.getSubscriber());

		final Long[] actionGroupIds = subscrGroupIds;

		ApiActionLocation userAction = new ApiActionEntityLocationAdapter<SubscrActionUser, Long>(entity, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscrActionUser processAndReturnResult() {
				return subscrActionUserService.createOne(entity, actionGroupIds);
			}

		};

		return WebApiHelper.processResponceApiActionCreate(userAction);

	}

	/**
	 *
	 * @param reportTemplareId
	 * @param reportTemplate
	 * @return
	 */
	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteOneUser(@PathVariable("id") long id) {
		final long finalId = id;
		ApiAction action = new ApiActionAdapter() {
			@Override
			public void process() {
				subscrActionUserService.deleteOne(finalId);
			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

}
