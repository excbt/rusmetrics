package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

/**
 * Контроллер для работы с пользователями абонентов
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.10.2015
 *
 */
@Controller
@RequestMapping("/api/rma")
public class RmaSubscrUserController extends SubscrUserController {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscrUserController.class);

	public class UsernameCheck {
		private final boolean userValid;
		private final boolean userExists;

		private UsernameCheck(String username, boolean userExists) {
			this.userExists = userExists;
			this.userValid = usernameValidator.validate(username);
		}

		public boolean isUserValid() {
			return userValid;
		}

		public boolean isUserExists() {
			return userExists;
		}
	}

	/**
	 *
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/{rSubscriberId}/subscrUsers", method = RequestMethod.GET)
	public ResponseEntity<?> getSubscrUsers(@PathVariable("rSubscriberId") Long rSubscriberId) {
		checkNotNull(rSubscriberId);

		if (!currentSubscriberService.isRma()) {
			ApiResponse.responseForbidden();
		}

		Subscriber subscriber = subscriberService.selectSubscriber(rSubscriberId);
		if (subscriber == null || subscriber.getRmaSubscriberId() == null
				|| !subscriber.getRmaSubscriberId().equals(getCurrentSubscriberId())) {
			return ApiResponse.responseBadRequest();
		}

		List<SubscrUser> subscrUsers = subscrUserService.selectBySubscriberId(rSubscriberId);
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(subscrUsers));
	}

	/**
	 *
	 * @param subscrUser
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{rSubscriberId}/subscrUsers", method = RequestMethod.POST)
	public ResponseEntity<?> createSubscrUser(@PathVariable("rSubscriberId") Long rSubscriberId,
			@RequestParam(value = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin,
			@RequestParam(value = "isReadonly", required = false, defaultValue = "false") Boolean isReadonly,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestBody SubscrUser subscrUser, HttpServletRequest request) {

		Subscriber subscriber = subscriberService.selectSubscriber(rSubscriberId);
		if (subscriber == null) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest("Subscriber is not found"));
		}

		return createSubscrUserInternal(subscriber, isAdmin, isReadonly, subscrUser, newPassword, request);
	}

	/**
	 *
	 * @param subscrUserId
	 * @param subscrUser
	 * @return
	 */
	@RequestMapping(value = "/{rSubscriberId}/subscrUsers/{subscrUserId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateSubscrUser(@PathVariable("rSubscriberId") Long rSubscriberId,
			@PathVariable("subscrUserId") Long subscrUserId,
			@RequestParam(value = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin,
			@RequestParam(value = "isReadonly", required = false, defaultValue = "false") Boolean isReadonly,
			@RequestParam(value = "oldPassword", required = false) String oldPassword,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestBody SubscrUser subscrUser) {

		//		String[] passwords = oldPassword != null && newPassword != null ? new String[] { oldPassword, newPassword }
		//				: null;
		String[] passwords = newPassword != null ? new String[] { oldPassword, newPassword } : null;

		Subscriber subscriber = subscriberService.selectSubscriber(rSubscriberId);
		if (subscriber == null) {
			return ApiResponse.responseBadRequest(ApiResult.badRequest("Subscriber is not found"));
		}

		return updateSubscrUserInternal(subscriber, subscrUserId, isAdmin, isReadonly, subscrUser, passwords);
	}

	/**
	 *
	 * @param subscrUserId
	 * @param isPermanent
	 * @return
	 */
	@RequestMapping(value = "/{rSubscriberId}/subscrUsers/{subscrUserId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSubscrUser(@PathVariable("rSubscriberId") Long rSubscriberId,
			@PathVariable("subscrUserId") Long subscrUserId,
			@RequestParam(value = "isPermanent", required = false, defaultValue = "false") Boolean isPermanent) {

		return deleteSubscrUserInternal(rSubscriberId, subscrUserId, isPermanent);

	}

	/**
	 *
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers/checkExists", method = RequestMethod.GET)
	public ResponseEntity<?> getSubscrUsersCheck(@RequestParam("username") String username) {
		checkNotNull(username);

		Optional<SubscrUser> userOptional = subscrUserService.findByUsername(username);
		UsernameCheck validator = new UsernameCheck(username, userOptional.isPresent());

		return ApiResponse.responseOK(validator);
	}

}
