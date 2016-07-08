package ru.excbt.datafuse.nmk.web.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.web.api.support.ApiResult;

@Controller
@RequestMapping(value = "/api/securityCheck")
public class SecurityCheckController extends WebApiController {

	private boolean isAuthenthicated() {
		return SecurityContextHolder.getContext().getAuthentication() != null &&
				SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
				//when Anonymous Authentication is enabled
				!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/isAuthenticated", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getIsAuthenticated() {
		if (isAuthenthicated()) {
			return responseOK(ApiResult.ok("Is Authenticated OK"));
		}
		return responseForbidden();
	}

}
