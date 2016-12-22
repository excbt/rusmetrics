/**
 * 
 */
package ru.excbt.datafuse.nmk.ldap.service;

import org.springframework.ldap.NamingException;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
public class SubscrLdapException extends NamingException {

	/**
	 * @param msg
	 * @param cause
	 */
	public SubscrLdapException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * 
	 * @param msg
	 */
	public SubscrLdapException(String msg) {
		super(msg);
	}

}
