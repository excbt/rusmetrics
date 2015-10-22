package ru.excbt.datafuse.nmk.ldap.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.ContextExecutor;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;

@Service
public class LdapService {

	public static final String BASE_DN = "dc=nmk,dc=ru";

	private static final Logger logger = LoggerFactory.getLogger(LdapService.class);

	private class LdapProps {
		private final static String INET_USER_STATUS = "InetUserStatus";
		private final static String INET_USER_STATUS_ACTIVE = "Active";
		private final static String INET_USER_STATUS_INACTIVE = "Inactive";
	}

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
	public boolean changePassword(String username, String oldPassword, String newPassword) {

		boolean oldPasswordCheck = doAuthentificate(username, oldPassword);

		if (!oldPasswordCheck) {
			return false;
		}

		return changePassword(username, newPassword);
	}

	/**
	 * 
	 * @param user
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public boolean changePassword(LdapUserAccount user, String oldPassword, String newPassword) {
		return changePassword(user.getUserName(), oldPassword, newPassword);
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
			public Object executeWithContext(DirContext ctx) throws NamingException {
				if (!(ctx instanceof LdapContext)) {
					throw new IllegalArgumentException(
							"Extended operations require LDAPv3 - " + "Context must be of type LdapContext");
				}
				LdapContext ldapContext = (LdapContext) ctx;
				ExtendedRequest er = new ModifyPasswordRequest(dnString, newPassword);
				return ldapContext.extendedOperation(er);
			}
		});

		return true;
	}

	/**
	 * 
	 * @param user
	 * @param newPassword
	 * @return
	 */
	public boolean changePassword(LdapUserAccount user, String newPassword) {
		return changePassword(user.getUserName(), newPassword);
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
		List<Object> result = ldapTemplate.search(LdapUtils.emptyLdapName(), f.toString(),
				new AbstractContextMapper<Object>() {
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

	/**
	 * 
	 * @param username
	 * @param email
	 * @return
	 * @throws InvalidNameException
	 */
	public void updateEMail(String rmaOu, String username, String email) throws InvalidNameException {
		Name dn = buildDn(rmaOu, username);

		// LdapName ldapName = new LdapName(getDnForUser(username));
		// logger.trace("username dn__:{}", getDnForUser(username));
		// logger.trace("username dn_2:{}", dn.toString());
		// logger.trace("username dn_3:{}", ldapName.toString());
		DirContextOperations context = ldapTemplate.lookupContext(dn);
		context.setAttributeValue("mail", email);
		ldapTemplate.modifyAttributes(context);

	}

	/**
	 * 
	 * @param username
	 * @throws InvalidNameException
	 */
	public void updateEMail(String rmaOu, String username) throws InvalidNameException {
		updateEMail(rmaOu, username, username + "@rusmetrics.ru");
	}

	/**
	 * 
	 * @param rmaOu
	 * @param username
	 * @return
	 */
	protected Name buildDn(String rmaOu, String username) {
		checkNotNull(username);
		checkNotNull(rmaOu);
		return LdapNameBuilder.newInstance().add("ou", "people").add("ou", rmaOu).add("uid", username).build();

	}

	protected Name buildDn(LdapUserAccount user) {
		checkNotNull(user.getUserName());
		checkNotNull(user.getOu());
		return LdapNameBuilder.newInstance().add("ou", "people").add("ou", user.getOu()).add("uid", user.getUserName())
				.build();

	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	public LdapUserAccount createUser(LdapUserAccount user) {
		checkNotNull(user);
		Name dn = buildDn(user);
		DirContextAdapter context = new DirContextAdapter(dn);

		context.setAttributeValues("objectclass", LdapUserAccount.OBJECT_CLASS);
		context.setAttributeValue("cn", user.getUserName());
		context.setAttributeValue("uid", user.getUserName());
		context.setAttributeValue("givenName", user.getFirstName());
		context.setAttributeValue("sn", user.getSecondName());
		context.setAttributeValue("homeDirectory", user.getHomeDirectory());
		context.setAttributeValue("mail", user.getMail());
		context.setAttributeValue("uidNumber", user.getUidNumber());
		context.setAttributeValue("gidNumber", user.getGidNumber());
		ldapTemplate.bind(context);
		return user;
	}

	/**
	 * 
	 * @param user
	 */
	public void blockLdapUser(LdapUserAccount user) {
		Name dn = buildDn(user);

		DirContextOperations context = ldapTemplate.lookupContext(dn);
		context.setAttributeValues("objectclass", LdapUserAccount.OBJECT_CLASS);
		context.setAttributeValue(LdapProps.INET_USER_STATUS, LdapProps.INET_USER_STATUS_INACTIVE);
		ldapTemplate.modifyAttributes(context);
	}

	/**
	 * 
	 * @param user
	 */
	public void unblockLdapUser(LdapUserAccount user) {
		Name dn = buildDn(user);
		DirContextOperations context = ldapTemplate.lookupContext(dn);
		context.setAttributeValues("objectclass", LdapUserAccount.OBJECT_CLASS);
		context.setAttributeValue(LdapProps.INET_USER_STATUS, LdapProps.INET_USER_STATUS_ACTIVE);
		ldapTemplate.modifyAttributes(context);
	}

}
