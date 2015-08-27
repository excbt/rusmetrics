package ru.excbt.datafuse.nmk.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
		grantedAuths.add(new SimpleGrantedAuthority(
				SecuredRoles.ROLE_SUBSCR_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(
				SecuredRoles.ROLE_SUBSCR_USER));
		grantedAuths.add(new SimpleGrantedAuthority(
				SecuredRoles.ROLE_CONT_OBJECT_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(
				SecuredRoles.ROLE_ZPOINT_ADMIN));
		return grantedAuths;
	}
}
