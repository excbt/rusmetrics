package ru.excbt.datafuse.nmk.config.security;

public final class RolesAccess {
	private RolesAccess() {

	}

	public final static String API_SUBSR_ACCESS = "hasAnyRole('ADMIN','SUBSCR_ADMIN','SUBSCR_USER')";
	public final static String API_RMA_ACCESS = "hasAnyRole('ADMIN','SUBSCR_RMA')";

}
