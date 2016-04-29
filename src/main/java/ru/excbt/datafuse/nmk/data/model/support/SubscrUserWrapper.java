package ru.excbt.datafuse.nmk.data.model.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscrUserWrapper {

	@JsonUnwrapped
	private SubscrUser subscrUser;

	private String passwordPocket;

	/**
	 * 
	 */
	public SubscrUserWrapper() {
	}

	/**
	 * 
	 * @param subscrUser
	 */
	public SubscrUserWrapper(SubscrUser subscrUser) {
		this.subscrUser = subscrUser;
	}

	public SubscrUser getSubscrUser() {
		return subscrUser;
	}

	public void setSubscrUser(SubscrUser subscrUser) {
		this.subscrUser = subscrUser;
	}

	public String getPasswordPocket() {
		return passwordPocket;
	}

	public void setPasswordPocket(String passwordPocket) {
		this.passwordPocket = passwordPocket;
	}

}
