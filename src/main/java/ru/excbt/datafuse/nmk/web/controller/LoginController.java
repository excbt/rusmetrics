package ru.excbt.datafuse.nmk.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import ru.excbt.datafuse.nmk.config.security.SamlSecurityConfig;

@Controller
public class LoginController {

	@Autowired(required = false)
	private SamlSecurityConfig samlSecurityConfig;

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/localLogin", method = { RequestMethod.GET,
			RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT })
	public ModelAndView doLocalLogin() {
		return new ModelAndView("login.jsp");
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login", method = { RequestMethod.GET,
			RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT })
	public String doLogin() {

		if (samlSecurityConfig == null
				|| samlSecurityConfig.isForceLocalLogin()) {
			return "redirect:/localLogin";
		}

		return "redirect:/saml/login";
	}
}
