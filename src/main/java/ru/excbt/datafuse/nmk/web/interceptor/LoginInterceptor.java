package ru.excbt.datafuse.nmk.web.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory
			.getLogger(LoginInterceptor.class);


	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();

//		SessionUser sessionUser = (SessionUser) session
//				.getAttribute(SessionUser.SESSION_USER_ATTR);
//		if (sessionUser == null) {
//			Authentication auth = SecurityContextHolder.getContext()
//					.getAuthentication();
//
//			AuditUser auditUser = auditUserService.findByUserName(auth
//					.getName());
//
//			if (auditUser == null) {
//
//			}
//
//			sessionUser = new SessionUser(auditUser.getId(),
//					auditUser.getUserName());
//			session.setAttribute(SessionUser.SESSION_USER_ATTR, sessionUser);
//		}

		return super.preHandle(request, response, handler);
	}
}
