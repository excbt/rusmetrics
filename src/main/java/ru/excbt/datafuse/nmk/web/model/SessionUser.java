package ru.excbt.datafuse.nmk.web.model;

import org.joda.time.DateTime;

public class SessionUser {

	public static final String SESSION_USER_ATTR = "sessionUser";	
	
	private final long userId;
	private final String userName;
	private final DateTime sessionDate;

	public SessionUser(long userId, String userName) {
		this.userId = userId;
		this.userName = userName;
		this.sessionDate = DateTime.now();
	}

	public long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public DateTime getSessionDate() {
		return sessionDate;
	}
}
