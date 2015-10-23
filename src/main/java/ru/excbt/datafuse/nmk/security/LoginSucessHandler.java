package ru.excbt.datafuse.nmk.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.service.SubscrUserLoginLogService;

@Service
public class LoginSucessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(LoginSucessHandler.class);

	@Autowired
	private SubscrUserLoginLogService subscrUserLoginLogService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {

		SubscriberUserDetails user = (SubscriberUserDetails) authentication.getPrincipal();
		// record login success of user

		logger.info("AK!!! onAuthenticationSuccess");

		subscrUserLoginLogService.registerLogin(user.getId(), user.getUsername());

		super.onAuthenticationSuccess(request, response, authentication);
	}

}