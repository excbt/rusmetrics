 package ru.excbt.datafuse.nmk.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;



public class LoginInterceptor extends HandlerInterceptorAdapter {

	public static final String SESSION_USER_ATTR = "sessionUser"; 
	
	private static final Logger logger = LoggerFactory
			.getLogger(LoginInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();

//		SystemUser user = (SystemUser) session.getAttribute(SESSION_USER_ATTR);
//		if (user == null) {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			
//			user = new SystemUser();
//			user.setUserName(auth.getName());
//			session.setAttribute(SESSION_USER_ATTR, user);
//		}
		
		return super.preHandle(request, response, handler);
	}
}
