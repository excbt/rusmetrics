package ru.excbt.datafuse.nmk.web.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.support.UsernameValidator;
import ru.excbt.datafuse.nmk.data.service.SubscrRoleService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.web.api.support.ApiAction;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionLocation;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionAdapter;
import ru.excbt.datafuse.nmk.web.api.support.EntityApiActionLocationAdapter;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

@Controller
@RequestMapping("/api/subscr")
public class SubscrUserController extends SubscrApiController {

	private static final Logger logger = LoggerFactory.getLogger(SubscrUserController.class);

	protected final UsernameValidator usernameValidator = new UsernameValidator();

	@Autowired
	protected SubscrUserService subscrUserService;

	@Autowired
	protected SubscrRoleService subscrRoleService;

	/**
	 * 
	 * @param rSubscriberId
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentSubscrUsers() {
		List<SubscrUser> subscrUsers = subscrUserService.findBySubscriberId(getSubscriberId());
		return responseOK(ObjectFilters.deletedFilter(subscrUsers));
	}

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers/{subscrUserId}", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentSubscrUser(@PathVariable("subscrUserId") Long subscrUserId) {
		checkNotNull(subscrUserId);

		SubscrUser subscrUser = subscrUserService.findOne(subscrUserId);
		if (subscrUser == null || subscrUser.getSubscriberId() == null
				|| !subscrUser.getSubscriberId().equals(subscrUserId)) {
			return responseBadRequest();
		}

		List<SubscrUser> subscrUsers = subscrUserService.findBySubscriberId(getSubscriberId());
		return responseOK(ObjectFilters.deletedFilter(subscrUsers));
	}

	/**
	 * 
	 * @param subscrUser
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers", method = RequestMethod.POST)
	public ResponseEntity<?> createCurrentSubscrUsers(
			@RequestParam(value = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestBody SubscrUser subscrUser, HttpServletRequest request) {

		return createSubscrUserInternal(getSubscriberId(), isAdmin, subscrUser, newPassword, request);
	}

	/**
	 * 
	 * @param subscrUserId
	 * @param subscrUser
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers/{subscrUserId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateCurrentSubscrUsers(@PathVariable("subscrUserId") Long subscrUserId,
			@RequestParam(value = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin,
			@RequestParam(value = "oldPassword", required = false) String oldPassword,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestBody SubscrUser subscrUser) {

		String[] passwords = oldPassword != null && newPassword != null ? new String[] { oldPassword, newPassword }
				: null;

		return updateSubscrUserInternal(getSubscriberId(), subscrUserId, isAdmin, subscrUser, passwords);
	}

	/**
	 * 
	 * @param subscrUserId
	 * @param isPermanent
	 * @return
	 */
	@RequestMapping(value = "/subscrUsers/{subscrUserId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCurrentSubscrUsers(@PathVariable("subscrUserId") Long subscrUserId,
			@RequestParam(value = "isPermanent", required = false, defaultValue = "false") Boolean isPermanent) {

		return deleteSubscrUserInternal(getSubscriberId(), subscrUserId, isPermanent);

	}

	/**
	 * 
	 * @param rSubscriberId
	 * @param isAdmin
	 * @param subscrUser
	 * @param request
	 * @return
	 */
	protected ResponseEntity<?> createSubscrUserInternal(Long rSubscriberId, Boolean isAdmin, SubscrUser subscrUser,
			String password, HttpServletRequest request) {
		checkNotNull(rSubscriberId);
		checkNotNull(subscrUser);

		if (subscrUser.getUserName() != null) {
			subscrUser.setUserName(subscrUser.getUserName().toLowerCase());
		}

		if (!usernameValidator.validate(subscrUser.getUserName())) {
			return responseBadRequest(ApiResult.validationError(
					"Username %s is not valid. " + "Min length is 3, max length is 20. Allowed characters: {a-z0-9_-]}",
					subscrUser.getUserName()));
		}

		if (subscrUser.getFirstName() == null || subscrUser.getFirstName().length() == 0) {
			return responseBadRequest(ApiResult.validationError("firstName is not valid"));
		}

		if (subscrUser.getLastName() == null || subscrUser.getLastName().length() == 0) {
			return responseBadRequest(ApiResult.validationError("lastName is not valid"));
		}

		subscrUser.setSubscriberId(rSubscriberId);
		subscrUser.getSubscrRoles().clear();
		subscrUser.setIsAdmin(isAdmin);

		if (isAdmin) {
			subscrUser.getSubscrRoles().addAll(subscrRoleService.subscrAdminRoles());
		} else {
			subscrUser.getSubscrRoles().addAll(subscrRoleService.subscrUserRoles());
		}

		ApiActionLocation action = new EntityApiActionLocationAdapter<SubscrUser, Long>(subscrUser, request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getId();
			}

			@Override
			public SubscrUser processAndReturnResult() {
				return subscrUserService.createOne(entity, password);
			}
		};

		return WebApiHelper.processResponceApiActionCreate(action);
	}

	/**
	 * 
	 * @param rSubscriberId
	 * @param subscrUserId
	 * @param isAdmin
	 * @param subscrUser
	 * @return
	 */
	protected ResponseEntity<?> updateSubscrUserInternal(Long rSubscriberId, Long subscrUserId, Boolean isAdmin,
			SubscrUser subscrUser, String[] passwords) {

		checkNotNull(rSubscriberId);
		checkNotNull(subscrUserId);
		checkNotNull(subscrUser);
		checkNotNull(subscrUser.getSubscriberId());

		if (!subscrUser.getSubscriberId().equals(rSubscriberId)) {
			return responseBadRequest();
		}

		if (checkSubscrUserOwnerFail(rSubscriberId, subscrUser)) {
			return responseBadRequest();
		}

		if (subscrUser.getFirstName() == null || subscrUser.getFirstName().length() == 0) {
			return responseBadRequest(ApiResult.validationError("firstName is not valid"));
		}

		if (subscrUser.getLastName() == null || subscrUser.getLastName().length() == 0) {
			return responseBadRequest(ApiResult.validationError("lastName is not valid"));
		}

		subscrUser.getSubscrRoles().clear();
		subscrUser.setIsAdmin(isAdmin);

		if (isAdmin) {
			subscrUser.getSubscrRoles().addAll(subscrRoleService.subscrAdminRoles());
		} else {
			subscrUser.getSubscrRoles().addAll(subscrRoleService.subscrUserRoles());
		}

		ApiAction action = new EntityApiActionAdapter<SubscrUser>(subscrUser) {

			@Override
			public SubscrUser processAndReturnResult() {
				return subscrUserService.updateOne(entity, passwords);
			}
		};

		return WebApiHelper.processResponceApiActionUpdate(action);
	}

	/**
	 * 
	 * @param rSubscriberId
	 * @param subscrUserId
	 * @param isPermanent
	 * @return
	 */
	protected ResponseEntity<?> deleteSubscrUserInternal(Long rSubscriberId, Long subscrUserId, Boolean isPermanent) {
		checkNotNull(rSubscriberId);
		checkNotNull(subscrUserId);

		SubscrUser subscrUser = subscrUserService.findOne(subscrUserId);
		if (checkSubscrUserOwnerFail(rSubscriberId, subscrUser)) {
			return responseBadRequest();
		}

		ApiAction action = new ApiActionAdapter() {

			@Override
			public void process() {
				if (Boolean.TRUE.equals(isPermanent)) {
					subscrUserService.deleteOnePermanent(subscrUserId);
				} else {
					subscrUserService.deleteOne(subscrUserId);
				}

			}
		};

		return WebApiHelper.processResponceApiActionDelete(action);
	}

	/**
	 * 
	 * @param rSubscriberId
	 * @param subscrUser
	 * @return
	 */
	private boolean checkSubscrUserOwnerFail(Long rSubscriberId, SubscrUser subscrUser) {
		return subscrUser == null || subscrUser.getSubscriberId() == null
				|| !subscrUser.getSubscriberId().equals(rSubscriberId);
	}

}
