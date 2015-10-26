package ru.excbt.datafuse.nmk.ldap.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class LdapUserAccount {

	public final static String[] OBJECT_CLASS = new String[] { "top", "person", "organizationalPerson", "inetOrgPerson",
			"posixAccount", "inetuser" };

	private final static String DEFAULT_EMAIL_DOMAIN = "@rusmetrics.ru";
	private final static String HOME_DIR = "/home/";

	private final Long id;
	private final String ou;
	private final String firstName;
	private final String secondName;
	private final String userName;
	private final String mail;
	private final String homeDirectory;
	private final String uidNumber;
	private final String gidNumber;

	/**
	 * 
	 * @param ou
	 * @param username
	 * @param names
	 * @param mail
	 */
	public LdapUserAccount(Long id, String userName, String[] names, String ou, String mail) {
		checkNotNull(ou);
		checkNotNull(id);
		checkNotNull(userName);
		checkNotNull(names);
		checkArgument(names.length == 2);

		this.id = id;
		this.ou = ou;
		this.userName = userName;
		this.firstName = names[0];
		this.secondName = names[1];
		this.mail = mail != null ? mail : userName + DEFAULT_EMAIL_DOMAIN;
		this.homeDirectory = HOME_DIR + userName;
		this.uidNumber = id.toString();
		this.gidNumber = Integer.valueOf(0).toString();
	}

	/**
	 * 
	 * @param ou
	 * @param username
	 * @param names
	 */
	public LdapUserAccount(Long id, String username, String[] names, String ou) {
		this(id, username, names, ou, username + DEFAULT_EMAIL_DOMAIN);
	}

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
	public String getOu() {
		return ou;
	}

	public Long getId() {
		return id;
	}

}
