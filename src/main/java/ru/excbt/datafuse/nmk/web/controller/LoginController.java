package ru.excbt.datafuse.nmk.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

	@RequestMapping(value = "/localLogin", method = { RequestMethod.GET,
			RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT })
	public ModelAndView doLocalLogin() {
		return new ModelAndView("login.jsp");
	}

	@RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST,
			RequestMethod.DELETE, RequestMethod.PUT })
	public String doSamlLogin() {
		return "redirect:/saml/login";
	}
}
