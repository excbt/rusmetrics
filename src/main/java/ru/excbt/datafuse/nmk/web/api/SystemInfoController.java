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

import ru.excbt.datafuse.nmk.data.model.FullUserInfo;
import ru.excbt.datafuse.nmk.data.service.support.CurrentUserService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Controller
@RequestMapping(value = "/api/systemInfo")
public class SystemInfoController extends WebApiController {

	protected class ReadOnlyMode {
		final boolean readOnlyMode;

		private ReadOnlyMode(boolean arg) {
			this.readOnlyMode = arg;
		}

		public boolean isReadOnlyMode() {
			return readOnlyMode;
		}
	}

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

		return responseOK(new SamlAuthMode(
				auth.getCredentials() instanceof SAMLCredential));
	}

}
