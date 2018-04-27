package ru.excbt.datafuse.nmk.web.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.SubscrUserWrapper;
import ru.excbt.datafuse.nmk.data.model.support.UsernameValidator;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrRoleService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Контроллер для работы с пользователями абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 14.10.2015
 *
 */
@Controller
@RequestMapping("/api/subscr")
public class SubscrUserController  {

	private static final Logger logger = LoggerFactory.getLogger(SubscrUserController.class);

	protected final UsernameValidator usernameValidator = new UsernameValidator();

	protected final SubscrUserService subscrUserService;

	protected final SubscrRoleService subscrRoleService;

    protected final PortalUserIdsService portalUserIdsService;

    public SubscrUserController(SubscrUserService subscrUserService, SubscrRoleService subscrRoleService, PortalUserIdsService portalUserIdsService) {
        this.subscrUserService = subscrUserService;
        this.subscrRoleService = subscrRoleService;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
     *
     * @return
     */
	@RequestMapping(value = "/subscrUsers", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentSubscrUsers() {
		List<SubscrUser> subscrUsers = subscrUserService.selectBySubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(subscrUsers));
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
		if (subscrUser == null || subscrUser.getSubscriber().getId() == null
				|| !subscrUser.getSubscriber().getId().equals(subscrUserId)) {
			return ApiResponse.responseBadRequest();
		}

		List<SubscrUser> subscrUsers = subscrUserService.selectBySubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
		return ApiResponse.responseOK(ObjectFilters.deletedFilter(subscrUsers));
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
			@RequestParam(value = "isReadonly", required = false, defaultValue = "false") Boolean isReadonly,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestBody SubscrUser subscrUser, HttpServletRequest request) {

		return createSubscrUserInternal(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()), isAdmin, isReadonly, subscrUser, newPassword, request);
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
			@RequestParam(value = "isReadonly", required = false, defaultValue = "false") Boolean isReadonly,
			@RequestParam(value = "oldPassword", required = false) String oldPassword,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestBody SubscrUser subscrUser) {

		//String[] passwords = oldPassword != null && newPassword != null ? new String[] { oldPassword, newPassword }
		//		: null;

		String[] passwords = newPassword != null ? new String[] { oldPassword, newPassword } : null;

		return updateSubscrUserInternal(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()), subscrUserId, isAdmin, isReadonly, subscrUser,
				passwords);
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

		return deleteSubscrUserInternal(portalUserIdsService.getCurrentIds().getSubscriberId(), subscrUserId, isPermanent);

	}

//	/**
//	 * TODO Method has been moved to SubscrUserService
//	 * Should be deleted
//	 *
//	 * @param rmaSubscriber
//	 * @param subscrUser
//	 * @param isAdmin
//	 * @param isReadonly
//	 * @return
//	 */
//	@Deprecated
//	private List<SubscrRole> processSubscrRoles2(final Subscriber rmaSubscriber, final boolean isAdmin,
//			final boolean isReadonly) {
//		List<SubscrRole> subscrRoles = new ArrayList<>();
//
//		if (Boolean.TRUE.equals(isReadonly)) {
//			subscrRoles.addAll(subscrRoleService.subscrReadonlyRoles());
//		} else {
//			if (Boolean.TRUE.equals(isAdmin)) {
//				subscrRoles.addAll(
//						subscrRoleService.subscrAdminRoles(Boolean.TRUE.equals(rmaSubscriber.getCanCreateChild())));
//				if (Boolean.TRUE.equals(rmaSubscriber.getIsRma())) {
//					subscrRoles.addAll(subscrRoleService.subscrRmaAdminRoles(rmaSubscriber.getCanCreateChild()));
//				}
//			} else {
//				subscrRoles.addAll(subscrRoleService.subscrUserRoles());
//			}
//		}
//
//		Map<Long, SubscrRole> subscrRolesMap = new HashMap<>();
//		for (SubscrRole r : subscrRoles) {
//			subscrRolesMap.put(r.getId(), r);
//		}
//
//		return new ArrayList<>(subscrRolesMap.values());
//	}
//

    /**
     *
     * @param rmaSubscriber
     * @param isAdmin
     * @param isReadonly
     * @param subscrUser
     * @param password
     * @param request
     * @return
     */
	protected ResponseEntity<?> createSubscrUserInternal(Subscriber rmaSubscriber, Boolean isAdmin, Boolean isReadonly,
			final SubscrUser subscrUser, String password, HttpServletRequest request) {
		checkNotNull(rmaSubscriber);
		checkNotNull(rmaSubscriber.getId());
		checkNotNull(subscrUser);

		if (subscrUser.getUserName() != null) {
			subscrUser.setUserName(subscrUser.getUserName().toLowerCase());
		}

		if (!usernameValidator.validate(subscrUser.getUserName())) {
			return ApiResponse.responseBadRequest(ApiResult.validationError(
					"Username %s is not valid. " + "Min length is 3, max length is 20. Allowed characters: {a-z0-9_-]}",
					subscrUser.getUserName()));
		}

		Optional<SubscrUser> checkUser = subscrUserService.findByUsername(subscrUser.getUserName());
		if (checkUser.isPresent()) {
			return ApiResponse.responseBadRequest(ApiResult.build(ApiResultCode.ERR_USER_ALREADY_EXISTS));
		}

        subscrUser.setSubscriber(new Subscriber().id(rmaSubscriber.getId()));
//		subscrUser.setSubscriberId(rmaSubscriber.getId());
		subscrUser.setIsAdmin(isAdmin);
		subscrUser.setIsReadonly(isReadonly);
		if (isReadonly) {
			subscrUser.setIsAdmin(false);
		}

		List<SubscrRole> subscrRoles = subscrUserService.processSubscrRoles(rmaSubscriber, isAdmin, isReadonly);

		subscrUser.getSubscrRoles().clear();
		subscrUser.getSubscrRoles().addAll(subscrRoles);

		ApiActionLocation action = new ApiActionEntityLocationAdapter<SubscrUserWrapper, Long>(request) {

			@Override
			protected Long getLocationId() {
				return getResultEntity().getSubscrUser().getId();
			}

			@Override
			public SubscrUserWrapper processAndReturnResult() {
				SubscrUser result = subscrUserService.createSubscrUser(subscrUser, password);
				return new SubscrUserWrapper(result, true);
			}
		};

		return ApiActionTool.processResponceApiActionCreate(action);
	}

    /**
     *
     * @param rmaSubscriber
     * @param subscrUserId
     * @param isAdmin
     * @param isReadonly
     * @param subscrUser
     * @param passwords
     * @return
     */
	protected ResponseEntity<?> updateSubscrUserInternal(Subscriber rmaSubscriber, Long subscrUserId, Boolean isAdmin,
			Boolean isReadonly, final SubscrUser subscrUser, String[] passwords) {

		checkNotNull(rmaSubscriber);
		checkNotNull(rmaSubscriber.getId());
		checkNotNull(subscrUserId);
		checkNotNull(subscrUser);
		checkNotNull(subscrUser.getSubscriber());

		if (!subscrUser.getSubscriber().getId().equals(rmaSubscriber.getId())) {
			return ApiResponse.responseBadRequest();
		}

		if (checkSubscrUserOwnerFail(rmaSubscriber.getId(), subscrUser)) {
			return ApiResponse.responseBadRequest();
		}

		subscrUser.setIsAdmin(isAdmin);
		subscrUser.setIsReadonly(isReadonly);
		if (isReadonly) {
			subscrUser.setIsAdmin(false);
		}

		List<SubscrRole> subscrRoles = subscrUserService.processSubscrRoles(rmaSubscriber, isAdmin, isReadonly);

		subscrUser.getSubscrRoles().clear();
		subscrUser.getSubscrRoles().addAll(subscrRoles);

		ApiAction action = new ApiActionEntityAdapter<SubscrUserWrapper>() {

			@Override
			public SubscrUserWrapper processAndReturnResult() {
				SubscrUser result = subscrUserService.updateSubscrUser(subscrUser, passwords);
				return new SubscrUserWrapper(result, true);
			}
		};

		return ApiActionTool.processResponceApiActionUpdate(action);
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
			return ApiResponse.responseBadRequest();
		}

		ApiAction action = (ApiActionAdapter) () -> {
            if (Boolean.TRUE.equals(isPermanent)) {
                subscrUserService.deleteSubscrUserPermanent(subscrUserId);
            } else {
                subscrUserService.deleteSubscrUser(subscrUserId);
            }

        };

		return ApiActionTool.processResponceApiActionDelete(action);
	}

	/**
	 *
	 * @param rSubscriberId
	 * @param subscrUser
	 * @return
	 */
	private boolean checkSubscrUserOwnerFail(Long rSubscriberId, SubscrUser subscrUser) {
		return subscrUser == null || subscrUser.getSubscriber().getId() == null
				|| !subscrUser.getSubscriber().getId().equals(rSubscriberId);
	}

}
