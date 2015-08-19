package ru.excbt.datafuse.nmk.ldap.service;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.LdapContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextExecutor;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;

@Service
public class LdapService {

	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private LdapConfig ldapConfig;

	/**
	 * 
	 * @param username
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public boolean changePassword(String username, String oldPassword,
			String newPassword) {

		boolean oldPasswordCheck = doAuthentificate(username, oldPassword);

		if (!oldPasswordCheck) {
			return false;
		}

		return changePassword(username, newPassword);
	}

	/**
	 * 
	 * @param username
	 * @param newPassword
	 * @return
	 */
	public boolean changePassword(String username, String newPassword) {

		String dnString = getDnForUser(username);				

		ldapTemplate.executeReadOnly(new ContextExecutor<Object>() {
			@Override
			public Object executeWithContext(DirContext ctx)
					throws NamingException {
				if (!(ctx instanceof LdapContext)) {
					throw new IllegalArgumentException(
							"Extended operations require LDAPv3 - "
									+ "Context must be of type LdapContext");
				}
				LdapContext ldapContext = (LdapContext) ctx;
				ExtendedRequest er = new ModifyPasswordRequest(dnString,
						newPassword);
				return ldapContext.extendedOperation(er);
			}
		});

		return true;
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean doAuthentificate(String username, String password) {

		String uid = getUsernameUidString(username);

		boolean authenticated = ldapTemplate.authenticate("", uid, password);
		return authenticated;
	}

	private String getUsernameUidString(String username) {
		return String.format("(uid=%s)", username);
	}

	/**
	 * 
	 * @param uid
	 * @return
	 */
	public String getDnForUser(String uid) {
		
		Filter f = new EqualsFilter("uid", uid);
		List<Object> result = ldapTemplate.search(LdapUtils.emptyLdapName(),
				f.toString(), new AbstractContextMapper<Object>() {
					@Override
					protected Object doMapFromContext(DirContextOperations ctx) {
						return ctx.getNameInNamespace();
					}
				});

		if (result.size() != 1) {
			throw new RuntimeException("User not found or not unique. userName " + uid);
		}

		return (String) result.get(0);
	}

}
