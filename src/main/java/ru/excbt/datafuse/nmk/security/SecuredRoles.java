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
	String ROLE_ADMIN = "ROLE_ADMIN";
	String ROLE_SUBSCR = "ROLE_SUBSCR";
	String ROLE_SUBSCR_ADMIN = "ROLE_SUBSCR_ADMIN";
	String ROLE_SUBSCR_USER = "ROLE_SUBSCR_USER";
	String ROLE_SUBSCR_READONLY = "ROLE_SUBSCR_READONLY";
	String ROLE_CONT_OBJECT_ADMIN = "ROLE_CONT_OBJECT_ADMIN";
	String ROLE_ZPOINT_ADMIN = "ROLE_ZPOINT_ADMIN";
	String ROLE_DEVICE_OBJECT_ADMIN = "ROLE_DEVICE_OBJECT_ADMIN";
	String ROLE_RMA = "ROLE_RMA";
	String ROLE_RMA_CONT_OBJECT_ADMIN = "ROLE_RMA_CONT_OBJECT_ADMIN";
	String ROLE_RMA_ZPOINT_ADMIN = "ROLE_RMA_ZPOINT_ADMIN";
	String ROLE_RMA_DEVICE_OBJECT_ADMIN = "ROLE_RMA_DEVICE_OBJECT_ADMIN";
	String ROLE_RMA_SUBSCRIBER_ADMIN = "ROLE_RMA_SUBSCRIBER_ADMIN";
	String ROLE_SUBSCR_CREATE_CHILD = "ROLE_SUBSCR_CREATE_CHILD";
	String ROLE_SUBSCR_CREATE_CABINET = "ROLE_SUBSCR_CREATE_CABINET";
	String ROLE_CABINET = "ROLE_CABINET";
	String ROLE_CABINET_USER = "ROLE_CABINET_USER";
}
