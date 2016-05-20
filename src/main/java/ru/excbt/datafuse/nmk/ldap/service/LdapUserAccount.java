package ru.excbt.datafuse.nmk.ldap.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

/**
 * Класс для работы с пользователем LDAP
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.10.2015
 *
 */
public final class LdapUserAccount {

	public final static String[] OBJECT_CLASS = new String[] { "inetOrgPerson", "inetuser", "organizationalPerson",
			"person", "posixAccount", "sunFMSAML2NameIdentifier", "top" };

	private final static String DEFAULT_EMAIL_DOMAIN = "@rusmetrics.ru";
	private final static String HOME_DIR = "/home/";

	private final Long id;
	private final String[] orgUnits;
	private final String firstName;
	private final String secondName;
	private final String userName;
	private final String mail;
	private final String homeDirectory;
	private final String uidNumber;
	private final String gidNumber;
	private final String description;
	private final String businessCategory;

	/**
	 * 
	 * @param id
	 * @param userName
	 * @param names
	 * @param orgUnits
	 * @param mail
	 * @param description
	 */
	public LdapUserAccount(Long id, String userName, String[] names, String[] orgUnits, String mail,
			String description, String gidNumber, String businessCategory) {
		checkNotNull(orgUnits);
		checkNotNull(id);
		checkNotNull(userName);
		checkNotNull(names);
		checkArgument(names.length == 2);

		this.id = id;
		this.orgUnits = Arrays.copyOf(orgUnits, orgUnits.length);
		this.userName = userName;
		this.firstName = names[0];
		this.secondName = names[1];
		this.mail = mail != null ? mail : userName + DEFAULT_EMAIL_DOMAIN;
		this.homeDirectory = HOME_DIR + userName;
		this.uidNumber = id.toString();
		this.gidNumber = gidNumber != null ? gidNumber : Integer.valueOf(0).toString();
		this.description = description;
		this.businessCategory = businessCategory;
	}

	/**
	 * 
	 * @param ou
	 * @param username
	 * @param names
	 */
	//	public LdapUserAccount(Long id, String username, String[] names, String[] orgUnits) {
	//		this(id, username, names, orgUnits, username + DEFAULT_EMAIL_DOMAIN, null, null);
	//	}

	/**
	 * 
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * 
	 * @return
	 */
	public String getSecondName() {
		return secondName;
	}

	/**
	 * 
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 
	 * @return
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * 
	 * @return
	 */
	public String getHomeDirectory() {
		return homeDirectory;
	}

	/**
	 * 
	 * @return
	 */
	public String getUidNumber() {
		return uidNumber;
	}

	/**
	 * 
	 * @return
	 */
	public String getGidNumber() {
		return gidNumber;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getOrgUnits() {
		return Arrays.copyOf(orgUnits, orgUnits.length);
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getBusinessCategory() {
		return businessCategory;
	}

}
