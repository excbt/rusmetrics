package ru.excbt.datafuse.nmk.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.excbt.datafuse.nmk.data.service.CurrentSubscriberUserDetailsService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {

	@Autowired
	private CurrentSubscriberUserDetailsService currentSubscriberUserDetailsService;

	@RequestMapping(value = "/auth/logout", method = { RequestMethod.GET })
	public String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		Authentication auth = currentSubscriberUserDetailsService.getAuthentication();

        request.logout();

		if (auth != null && (auth.getCredentials() instanceof SAMLCredential)) {
			return "redirect:/saml/logout";
		}

		return "redirect:/auth/localLogin";
	}


    @RequestMapping(value = "/logout", method = { RequestMethod.GET })
    public String doLogoutOld(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        Authentication auth = currentSubscriberUserDetailsService.getAuthentication();

        request.logout();

        if (auth != null && (auth.getCredentials() instanceof SAMLCredential)) {
            return "redirect:/saml/logout";
        }

        return "redirect:/auth/localLogin";
    }

}
