package ru.excbt.datafuse.nmk.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Утилиты для работы с правами
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 05.08.2015
 *
 */
public class AdminUtils {

	private AdminUtils() {

	}

	/**
	 * 
	 * @return
	 */
	public static List<GrantedAuthority> makeAdminAuths() {
		List<GrantedAuthority> grantedAuths = new ArrayList<>();
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_SUBSCR_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_SUBSCR_USER));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_CONT_OBJECT_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_ZPOINT_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_DEVICE_OBJECT_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_RMA_CONT_OBJECT_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_RMA_DEVICE_OBJECT_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_RMA_SUBSCRIBER_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_RMA_ZPOINT_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_SUBSCR_CREATE_CHILD));
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_SUBSCR_CREATE_CABINET));
		return grantedAuths;
	}
}
