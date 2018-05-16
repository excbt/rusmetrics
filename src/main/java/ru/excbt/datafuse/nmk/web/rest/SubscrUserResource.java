package ru.excbt.datafuse.nmk.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.SubscrUserWrapper;
import ru.excbt.datafuse.nmk.service.SubscrUserManageService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.validators.UsernameValidator;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrRoleService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.service.dto.SubscrUserDTO;
import ru.excbt.datafuse.nmk.service.mapper.SubscrUserMapper;
import ru.excbt.datafuse.nmk.web.api.support.*;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;
import ru.excbt.datafuse.nmk.web.rest.support.ApiActionTool;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
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
@RequestMapping("/api")
public class SubscrUserResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscrUserResource.class);

	protected final UsernameValidator usernameValidator = new UsernameValidator();

	protected final SubscrUserService subscrUserService;

	protected final SubscrRoleService subscrRoleService;

    protected final PortalUserIdsService portalUserIdsService;

    protected final SubscrUserMapper subscrUserMapper;

    protected final SubscrUserManageService subscrUserManageService;

    protected final SubscriberService subscriberService;

    public SubscrUserResource(SubscrUserService subscrUserService, SubscrRoleService subscrRoleService, PortalUserIdsService portalUserIdsService, SubscrUserMapper subscrUserMapper, SubscrUserManageService subscrUserManageService, SubscriberService subscriberService) {
        this.subscrUserService = subscrUserService;
        this.subscrRoleService = subscrRoleService;
        this.portalUserIdsService = portalUserIdsService;
        this.subscrUserMapper = subscrUserMapper;
        this.subscrUserManageService = subscrUserManageService;
        this.subscriberService = subscriberService;
    }

    /**
     *
     * @return
     */
	@RequestMapping(value = "/subscr/subscrUsers", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentSubscrUsers() {
		List<SubscrUserDTO> subscrUsers = subscrUserService.findBySubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
		return ApiResponse.responseOK(subscrUsers);
	}

	/**
	 *
	 * @param subscrUserId
	 * @return
	 */
	@RequestMapping(value = "/subscr/subscrUsers/{subscrUserId}", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentSubscrUser(@PathVariable("subscrUserId") Long subscrUserId) {
		checkNotNull(subscrUserId);

		SubscrUserDTO subscrUser = subscrUserService.findOne(subscrUserId);
		if (subscrUser == null || subscrUser.getSubscriberId() == null
				|| !subscrUser.getSubscriberId().equals(subscrUserId)) {
			return ApiResponse.responseBadRequest();
		}

		List<SubscrUserDTO> subscrUsers = subscrUserService.findBySubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
		return ApiResponse.responseOK(subscrUsers);
	}

	/**
	 *
	 * @param subscrUserDTO
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/subscr/subscrUsers", method = RequestMethod.POST)
	public ResponseEntity<?> createCurrentSubscrUsers(
			@RequestParam(value = "isAdmin", required = false, defaultValue = "false") Boolean isAdmin,
			@RequestParam(value = "isReadonly", required = false, defaultValue = "false") Boolean isReadonly,
			@RequestParam(value = "newPassword", required = false) String newPassword,
			@RequestBody SubscrUserDTO subscrUserDTO, HttpServletRequest request) {

        subscrUserDTO.setIsAdmin(Boolean.TRUE.equals(isAdmin));
        subscrUserDTO.setIsReadonly(Boolean.TRUE.equals(isReadonly));
	    return createSubscrUserInternal(new Subscriber().id(portalUserIdsService.getCurrentIds().getSubscriberId()), subscrUserDTO, newPassword, request);
	}

	/**
	 *
	 * @param subscrUserId
	 * @param subscrUser
	 * @return
	 */
	@RequestMapping(value = "/subscr/subscrUsers/{subscrUserId}", method = RequestMethod.PUT)
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
	@RequestMapping(value = "/subscr/subscrUsers/{subscrUserId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCurrentSubscrUsers(@PathVariable("subscrUserId") Long subscrUserId,
			@RequestParam(value = "isPermanent", required = false, defaultValue = "false") Boolean isPermanent) {

		return deleteSubscrUserInternal(portalUserIdsService.getCurrentIds().getSubscriberId(), subscrUserId, isPermanent);

	}


    protected ResponseEntity<?> createSubscrUserInternal(Subscriber rmaSubscriber,
                                                           final SubscrUserDTO subscrUserDTO, String password, HttpServletRequest request) {
        subscrUserDTO.setSubscriberId(rmaSubscriber.getId());
	    Optional<SubscrUser> subscrUserOptional = subscrUserManageService.createSubscrUser(subscrUserDTO, password);
        return subscrUserOptional
            .map(SubscrUserDTO::new)
                    .map(r -> ResponseEntity.created(URI.create(request.getRequestURI() + '/' + r.getId())).body(r))
            .orElse(ResponseEntity.badRequest().build());
    }

//    /**
//     *
//     * @param rmaSubscriber
//     * @param isAdmin
//     * @param isReadonly
//     * @param subscrUser
//     * @param password
//     * @param request
//     * @return
//     */
//	protected ResponseEntity<?> createSubscrUserInternal22(Subscriber rmaSubscriber, Boolean isAdmin, Boolean isReadonly,
//			final SubscrUser subscrUser, String password, HttpServletRequest request) {
//		checkNotNull(rmaSubscriber);
//		checkNotNull(rmaSubscriber.getId());
//		checkNotNull(subscrUser);
//
//		if (subscrUser.getUserName() != null) {
//			subscrUser.setUserName(subscrUser.getUserName().toLowerCase());
//		}
//
//		if (!usernameValidator.validate(subscrUser.getUserName())) {
//			return ApiResponse.responseBadRequest(ApiResult.validationError(
//					"Username %s is not valid. " + "Min length is 3, max length is 20. Allowed characters: {a-z0-9_-]}",
//					subscrUser.getUserName()));
//		}
//
//		Optional<SubscrUser> checkUser = subscrUserService.findByUsername(subscrUser.getUserName());
//		if (checkUser.isPresent()) {
//			return ApiResponse.responseBadRequest(ApiResult.build(ApiResultCode.ERR_USER_ALREADY_EXISTS));
//		}
//
//        subscrUser.setSubscriber(new Subscriber().id(rmaSubscriber.getId()));
//		subscrUser.setIsAdmin(isAdmin);
//		subscrUser.setIsReadonly(isReadonly);
//		if (isReadonly) {
//			subscrUser.setIsAdmin(false);
//		}
//
//		List<SubscrRole> subscrRoles = subscrUserService.processSubscrRoles(rmaSubscriber, isAdmin, isReadonly);
//
//		subscrUser.getSubscrRoles().clear();
//		subscrUser.getSubscrRoles().addAll(subscrRoles);
//
//		ApiActionLocation action = new ApiActionEntityLocationAdapter<SubscrUserWrapper, Long>(request) {
//
//			@Override
//			protected Long getLocationId() {
//				return getResultEntity().getSubscrUser().getId();
//			}
//
//			@Override
//			public SubscrUserWrapper processAndReturnResult() {
//				SubscrUser result = subscrUserService.createSubscrUser(subscrUser, password);
//				return new SubscrUserWrapper(result, true);
//			}
//		};
//
//		return ApiActionTool.processResponceApiActionCreate(action);
//	}

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
//		checkNotNull(subscrUser.getSubscriber());

        subscrUser.setSubscriber(rmaSubscriber);


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

		SubscrUserDTO subscrUser = subscrUserService.findOne(subscrUserId);
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

	private boolean checkSubscrUserOwnerFail(Long rSubscriberId, SubscrUserDTO subscrUser) {
		return subscrUser == null || subscrUser.getSubscriberId() == null
				|| !subscrUser.getSubscriberId().equals(rSubscriberId);
	}


    /**
     *
     * @param optSubscriberId
     * @return
     */
    @Timed
    @ApiOperation("Get all subscr users of subscriber")
    @RequestMapping(value = "/subscr-users", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscrUsers(@RequestParam(value = "subscriberId", required = false) Optional<Long> optSubscriberId) {

        if (optSubscriberId.isPresent()) {
            boolean check = subscriberService.checkParentSubscriber(optSubscriberId.get(),portalUserIdsService.getCurrentIds());
            if (!check) {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        }
        Long selectSubscriberId = optSubscriberId.orElse(portalUserIdsService.getCurrentIds().getSubscriberId());
        List<SubscrUserDTO> subscrUsers = subscrUserService.findBySubscriberId(selectSubscriberId);
        return ResponseEntity.ok(subscrUsers);
    }

    @Timed
    @ApiOperation("Get all subscr users of subscriber")
    @RequestMapping(value = "/subscr-users/page", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscrUsersPage(@RequestParam(value = "subscriberId", required = false) Optional<Long> optSubscriberId,
                                                @RequestParam(name = "searchString", required = false) Optional<String> searchString,
                                                Pageable pageable) {

        if (optSubscriberId.isPresent()) {
            boolean check = subscriberService.checkParentSubscriber(optSubscriberId.get(),portalUserIdsService.getCurrentIds());
            if (!check) {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        }
        Long selectSubscriberId = optSubscriberId.orElse(portalUserIdsService.getCurrentIds().getSubscriberId());
        Page<SubscrUserDTO> subscrUsers = subscrUserService.findBySubscriberIdPaged(selectSubscriberId, searchString, pageable);
        return ResponseEntity.ok(subscrUsers);
    }

    @Timed
    @ApiOperation("Get all subscr users of subscriber")
    @RequestMapping(value = "/subscr-users/{subscrUserId}", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscrUser(@PathVariable("subscrUserId") Long subscrUserId) {
        checkNotNull(subscrUserId);

        SubscrUserDTO subscrUserDTO = subscrUserService.findOne(subscrUserId);
        if (checkSubscrUserOwnerFail(portalUserIdsService.getCurrentIds().getSubscriberId(), subscrUserDTO)) {
            return ApiResponse.responseForbidden();
        }
        return ApiResponse.responseOK(subscrUserDTO);
    }

    @Timed
    @ApiOperation("Check if username is not taken")
    @RequestMapping(value = "/subscr-users/check", method = RequestMethod.GET)
    public ResponseEntity<?> getSubscrUserCheck(@RequestParam("username") String username) {
        boolean result = subscrUserService.checkUserNotExists(username);
        Map<String, Boolean> resultJson = new HashMap<>();
        resultJson.put("result", result);
        return ResponseEntity.ok(resultJson);
    }

    /**
     *
     * @param newPassword
     * @param subscrUserDTO
     * @param request
     * @return
     */
    @Timed
    @ApiOperation("SubscrUser Create")
    @RequestMapping(value = "/subscr-users", method = RequestMethod.POST)
    public ResponseEntity<?> createSubscrUser(
        @RequestParam(value = "newPassword", required = false) String newPassword,
        @RequestBody SubscrUserDTO subscrUserDTO, HttpServletRequest request) {

        subscrUserDTO.setIsAdmin(Boolean.TRUE.equals(subscrUserDTO.getIsAdmin()));
        subscrUserDTO.setIsReadonly(Boolean.TRUE.equals(subscrUserDTO.getIsReadonly()));
        subscrUserDTO.setIsBlocked(Boolean.TRUE.equals(subscrUserDTO.getIsBlocked()));

        subscrUserDTO.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
        Optional<SubscrUser> subscrUserOptional = subscrUserManageService.createSubscrUser(subscrUserDTO, newPassword);
        return subscrUserOptional
            .map(SubscrUserDTO::new)
            .map(r -> ResponseEntity.created(URI.create(request.getRequestURI() + '/' + r.getId())).body(r))
            .orElse(ResponseEntity.badRequest().build());
    }

    @Timed
    @ApiOperation("SubscrUser Update")
    @RequestMapping(value = "/subscr-users", method = RequestMethod.PUT)
    public ResponseEntity<?> updateSubscrUser(@RequestParam(value = "oldPassword", required = false) String oldPassword,
                                              @RequestParam(value = "newPassword", required = false) String newPassword,
                                              @RequestBody SubscrUserDTO subscrUserDTO) {

        if (subscrUserDTO.getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        String[] passwords = (newPassword != null && newPassword.length() > 0) ? new String[] { oldPassword, newPassword } : null;

        Optional<SubscrUser> subscrUserOptional = subscrUserManageService.updateSubscrUser(subscrUserDTO, passwords);
        return subscrUserOptional
            .map(SubscrUserDTO::new)
            .map(r -> ResponseEntity.ok(r))
            .orElse(ResponseEntity.badRequest().build());
    }

}
