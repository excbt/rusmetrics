package ru.excbt.datafuse.nmk.security;

import java.util.HashSet;
import java.util.Set;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String SUBSCR = "ROLE_SUBSCR";
    public static final String SUBSCR_ADMIN = "ROLE_SUBSCR_ADMIN";
    public static final String SUBSCR_USER = "ROLE_SUBSCR_USER";
    public static final String SUBSCR_READONLY = "ROLE_SUBSCR_READONLY";
    public static final String CONT_OBJECT_ADMIN = "ROLE_CONT_OBJECT_ADMIN";
    public static final String ZPOINT_ADMIN = "ROLE_ZPOINT_ADMIN";
    public static final String DEVICE_OBJECT_ADMIN = "ROLE_DEVICE_OBJECT_ADMIN";
    public static final String RMA = "ROLE_RMA";
    public static final String RMA_CONT_OBJECT_ADMIN = "ROLE_RMA_CONT_OBJECT_ADMIN";
    public static final String RMA_ZPOINT_ADMIN = "ROLE_RMA_ZPOINT_ADMIN";
    public static final String RMA_DEVICE_OBJECT_ADMIN = "ROLE_RMA_DEVICE_OBJECT_ADMIN";
    public static final String RMA_SUBSCRIBER_ADMIN = "ROLE_RMA_SUBSCRIBER_ADMIN";
    public static final String SUBSCR_CREATE_CHILD = "ROLE_SUBSCR_CREATE_CHILD";
    public static final String SUBSCR_CREATE_CABINET = "ROLE_SUBSCR_CREATE_CABINET";
    public static final String CABINET = "ROLE_CABINET";
    public static final String CABINET_USER = "ROLE_CABINET_USER";

    private AuthoritiesConstants() {
    }

    public static Set<String> adminAuthorities() {
        Set<String> result = new HashSet<>();
        result.add(ADMIN);
        result.add(USER);

        result.add(SUBSCR_ADMIN);
        result.add(SUBSCR_USER);
        result.add(CONT_OBJECT_ADMIN);
        result.add(ZPOINT_ADMIN);
        result.add(DEVICE_OBJECT_ADMIN);
        result.add(RMA_CONT_OBJECT_ADMIN);
        result.add(RMA_DEVICE_OBJECT_ADMIN);
        result.add(RMA_SUBSCRIBER_ADMIN);
        result.add(RMA_ZPOINT_ADMIN);
        result.add(SUBSCR_CREATE_CHILD);
        result.add(SUBSCR_CREATE_CABINET);
        return result;
    }


    public static Set<String> systemAuthorities() {
        Set<String> result = new HashSet<>();
        result.add(ADMIN);
        result.add(USER);

        result.add(RMA);
        result.add(RMA_CONT_OBJECT_ADMIN);
        result.add(RMA_DEVICE_OBJECT_ADMIN);
        result.add(RMA_SUBSCRIBER_ADMIN);
        result.add(RMA_ZPOINT_ADMIN);


        result.add(SUBSCR_ADMIN);
        result.add(SUBSCR_USER);
        result.add(CONT_OBJECT_ADMIN);
        result.add(ZPOINT_ADMIN);
        result.add(DEVICE_OBJECT_ADMIN);
        result.add(SUBSCR_CREATE_CHILD);
        result.add(SUBSCR_CREATE_CABINET);

        return result;
    }

    public static Set<String> subscrAdminNoChild() {
        Set<String> result = new HashSet<>();
        result.add(SUBSCR);
        result.add(USER);
        result.add(SUBSCR_ADMIN);
        result.add(SUBSCR_USER);
        result.add(CONT_OBJECT_ADMIN);
        result.add(ZPOINT_ADMIN);
        result.add(DEVICE_OBJECT_ADMIN);

        return result;
    }

}
