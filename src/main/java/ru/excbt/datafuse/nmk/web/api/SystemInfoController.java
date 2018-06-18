package ru.excbt.datafuse.nmk.web.api;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.excbt.datafuse.nmk.data.model.V_FullUserInfo;
import ru.excbt.datafuse.nmk.data.model.dto.ExSystemDto;
import ru.excbt.datafuse.nmk.data.model.keyname.ExSystem;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.repository.keyname.ExSystemRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.rest.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionObjectProcess;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;
import ru.excbt.datafuse.nmk.web.rest.support.ApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Контроллер для получения информации по системе
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 28.04.2015
 *
 */
@Controller
@RequestMapping(value = "/api/systemInfo")
public class SystemInfoController {

    private final LdapService ldapService;

    private final SystemParamService systemParamService;

	private final SubscrUserService subscrUserService;

	private final CurrentSubscriberUserDetailsService currentSubscriberUserDetailsService;

	private final SessionRegistry sessionRegistry;

	private final ExSystemRepository exSystemRepository;

	private final ModelMapper modelMapper;

	private final CurrentSubscriberService currentSubscriberService;

    private final PortalUserIdsService portalUserIdsService;

    public SystemInfoController(LdapService ldapService, SystemParamService systemParamService, SubscrUserService subscrUserService, CurrentSubscriberUserDetailsService currentSubscriberUserDetailsService, SessionRegistry sessionRegistry, ExSystemRepository exSystemRepository, ModelMapper modelMapper, CurrentSubscriberService currentSubscriberService, PortalUserIdsService portalUserIdsService) {
        this.ldapService = ldapService;
        this.systemParamService = systemParamService;
        this.subscrUserService = subscrUserService;
        this.currentSubscriberUserDetailsService = currentSubscriberUserDetailsService;
        this.sessionRegistry = sessionRegistry;
        this.exSystemRepository = exSystemRepository;
        this.modelMapper = modelMapper;
        this.currentSubscriberService = currentSubscriberService;
        this.portalUserIdsService = portalUserIdsService;
    }


    /**
	 *
	 * @author kovtonyk
	 *
	 */
	protected class ReadOnlyMode {
		final boolean readOnlyMode;

		private ReadOnlyMode(boolean arg) {
			this.readOnlyMode = arg;
		}

		public boolean isReadOnlyMode() {
			return readOnlyMode;
		}
	}

	/**
	 *
	 * @author kovtonyk
	 *
	 */
	protected class SamlAuthMode {
		final boolean samlAuthMode;

		private SamlAuthMode(boolean arg) {
			this.samlAuthMode = arg;
		}

		public boolean isSamlAuthMode() {
			return samlAuthMode;
		}

	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/fullUserInfo", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getFullUserInfo() {

		V_FullUserInfo result = currentSubscriberService.getFullUserInfo();
		if (result == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(result);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/readOnlyMode", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getUserRights() {
		UserDetails userDetails = currentSubscriberService.getCurrentUserDetails();

		if (userDetails == null) {
			return ApiResponse.responseOK(new ReadOnlyMode(true));
		}

		Optional<String> adminRole = userDetails.getAuthorities().stream()
				.filter((i) -> SecuredRoles.ROLE_ADMIN.equals(i.getAuthority())
						|| SecuredRoles.ROLE_SUBSCR_ADMIN.equals(i.getAuthority()))
				.map((i) -> i.getAuthority()).findFirst();

		return ApiResponse.responseOK(new ReadOnlyMode(!adminRole.isPresent()));
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/samlAuthMode", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSamlAuth() {
		Authentication auth = currentSubscriberUserDetailsService.getAuthentication();

		if (auth == null) {
			return ApiResponse.responseOK(new SamlAuthMode(false));
		}

		return ApiResponse.responseOK(new SamlAuthMode(auth.getCredentials() instanceof SAMLCredential));
	}

	/**
	 *
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(value = "/passwordChange", method = RequestMethod.PUT, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updatePassword(
			@RequestParam(value = "oldPassword", required = true) final String oldPassword,
			@RequestParam(value = "newPassword", required = true) final String newPassword) {

		if (oldPassword == null || newPassword == null || oldPassword.equals(newPassword)) {
			return ApiResponse.responseBadRequest(ApiResult.validationError("request params is not valid"));
		}

		V_FullUserInfo fullUserInfo = currentSubscriberService.getFullUserInfo();
		if (fullUserInfo == null) {
			return ApiResponse.responseForbidden();
		}

		String username = fullUserInfo.getUserName();

		boolean changeResult = ldapService.changePassword(username, oldPassword, newPassword);
		if (changeResult) {
			subscrUserService.clearSubscrUserPassword(fullUserInfo.getId());
			return ApiResponse.responseOK(ApiResult.ok("Password successfully changed"));
		}

		return ApiResponse.responseBadRequest();
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/serverName", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getServerName() {
		String serverName = systemParamService.getParamValueAsString("SERVER_NAME");
		return ResponseEntity.ok(serverName);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/invalidateAllSessions", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getInvalidateSessions() {
		if (!portalUserIdsService.isSystemUser()) {
			return ApiResponse.responseForbidden();
		}

		List<SessionInformation> activeSessions = new ArrayList<>();
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
				activeSessions.add(session);
			}
		}

		activeSessions.forEach(i -> {
			i.expireNow();
		});

		return ApiResponse.responseOK(ApiResult.ok("Invalidated sessions: " + activeSessions.size()));
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/isCMode", method = RequestMethod.GET, produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getIsCMode() {
		return ApiResponse.responseOK(portalUserIdsService.getCurrentIds().getSubscrTypeKey().equals(SubscrTypeKey.TEST_CERTIFICATE));
	}

	/**
	 *
	 * @return
	 */
	@GetMapping(value = "/exSystem", produces = ApiConst.APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getExSystem() {
		List<ExSystem> exSystems = exSystemRepository.findAll();

		ApiActionObjectProcess actionProcess = () -> {
			return exSystems.stream().map(i -> modelMapper.map(i, ExSystemDto.class)).collect(Collectors.toList());
		};
		return ApiResponse.responseOK(actionProcess);

	}

}
