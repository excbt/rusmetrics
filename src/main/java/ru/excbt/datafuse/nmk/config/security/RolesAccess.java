package ru.excbt.datafuse.nmk.config.security;

import ru.excbt.datafuse.nmk.security.SecuredRoles;

public final class RolesAccess implements SecuredRoles {
	private RolesAccess() {

	}

	public final static String API_SUBSR_ACCESS = hasAnyRoleContructor(ROLE_ADMIN, ROLE_SUBSCR, ROLE_RMA,
			ROLE_CABINET);

	public final static String API_RMA_ACCESS = hasAnyRoleContructor(ROLE_ADMIN, ROLE_RMA);

	/**
	 * 
	 * @param roles
	 * @return
	 */
	private final static String hasAnyRoleContructor(String... roles) {
		StringBuilder sb = new StringBuilder();
		sb.append("hasAnyRole(");

		for (String r : roles) {
			sb.append("'");
			sb.append(r.substring("ROLE_".length(), r.length()));
			sb.append("',");
		}

		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

}
