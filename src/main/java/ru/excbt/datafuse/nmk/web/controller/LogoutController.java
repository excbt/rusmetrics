package ru.excbt.datafuse.nmk.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.service.support.CurrentUserService;

@Controller
public class LogoutController {

	@Autowired
	private CurrentUserService currentUserService;

	@RequestMapping(value = "/logout", method = { RequestMethod.GET })
	public String doGet() {

		Authentication auth = currentUserService.getAuthentication();

		if (auth != null && (auth.getCredentials() instanceof SAMLCredential)) {
			return "redirect:/saml/logout";
		}

		return "redirect:/local/logout";
	}
}
