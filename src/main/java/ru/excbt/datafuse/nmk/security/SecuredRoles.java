package ru.excbt.datafuse.nmk.security;

/**
 * Роли системы
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.03.2015
 *
 */
public interface SecuredRoles {
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_SUBSCR = "ROLE_SUBSCR";
	public static final String ROLE_SUBSCR_ADMIN = "ROLE_SUBSCR_ADMIN";
	public static final String ROLE_SUBSCR_USER = "ROLE_SUBSCR_USER";
	public static final String ROLE_SUBSCR_READONLY = "ROLE_SUBSCR_READONLY";
	public static final String ROLE_CONT_OBJECT_ADMIN = "ROLE_CONT_OBJECT_ADMIN";
	public static final String ROLE_ZPOINT_ADMIN = "ROLE_ZPOINT_ADMIN";
	public static final String ROLE_DEVICE_OBJECT_ADMIN = "ROLE_DEVICE_OBJECT_ADMIN";
	public static final String ROLE_RMA = "ROLE_RMA";
	public static final String ROLE_RMA_CONT_OBJECT_ADMIN = "ROLE_RMA_CONT_OBJECT_ADMIN";
	public static final String ROLE_RMA_ZPOINT_ADMIN = "ROLE_RMA_ZPOINT_ADMIN";
	public static final String ROLE_RMA_DEVICE_OBJECT_ADMIN = "ROLE_RMA_DEVICE_OBJECT_ADMIN";
	public static final String ROLE_RMA_SUBSCRIBER_ADMIN = "ROLE_RMA_SUBSCRIBER_ADMIN";
	public static final String ROLE_SUBSCR_CREATE_CHILD = "ROLE_SUBSCR_CREATE_CHILD";
	public static final String ROLE_SUBSCR_CREATE_CABINET = "ROLE_SUBSCR_CREATE_CABINET";
	public static final String ROLE_CABINET_USER = "ROLE_CABINET_USER";
}
