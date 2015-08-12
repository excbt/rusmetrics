package ru.excbt.datafuse.nmk.web.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.excbt.datafuse.nmk.data.model.FullUserInfo;
import ru.excbt.datafuse.nmk.data.service.support.CurrentUserService;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.web.api.support.ApiResult;

@Controller
@RequestMapping(value = "/api/systemInfo")
public class SystemInfoController extends WebApiController {

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

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private LdapService ldapService;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/fullUserInfo", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getFullUserInfo() {

		FullUserInfo result = currentUserService.getFullUserInfo();
		if (result == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(result);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/readOnlyMode", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getUserRights() {
		UserDetails userDetails = currentUserService.getCurrentUserDetails();

		if (userDetails == null) {
			return responseOK(new ReadOnlyMode(true));
		}

		Optional<String> adminRole = userDetails
				.getAuthorities()
				.stream()
				.filter((i) -> SecuredRoles.ROLE_ADMIN.equals(i.getAuthority())
						|| SecuredRoles.ROLE_SUBSCR_ADMIN.equals(i
								.getAuthority())).map((i) -> i.getAuthority())
				.findFirst();

		return responseOK(new ReadOnlyMode(!adminRole.isPresent()));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/samlAuthMode", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getSamlAuth() {
		Authentication auth = currentUserService.getAuthentication();

		if (auth == null) {
			responseOK(new SamlAuthMode(false));
		}

		return responseOK(new SamlAuthMode(
				auth.getCredentials() instanceof SAMLCredential));
	}

	/**
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(value = "/passwordChange", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updatePassword(
			@RequestParam(value = "oldPassword", required = true) final String oldPassword,
			@RequestParam(value = "newPassword", required = true) final String newPassword) {

		if (oldPassword == null || newPassword == null
				|| oldPassword.equals(newPassword)) {
			return responseBadRequest(ApiResult
					.validationError("request params is not valid"));
		}

		FullUserInfo fullUserInfo = currentUserService.getFullUserInfo();
		if (fullUserInfo == null) {
			return responseForbidden();
		}

		String username = fullUserInfo.getUserName();

		boolean changeResult = ldapService.changePassword(username,
				oldPassword, newPassword);
		if (changeResult) {
			return responseOK(ApiResult.ok("Password successfully changed"));
		}

		return responseBadRequest();
	}
}
