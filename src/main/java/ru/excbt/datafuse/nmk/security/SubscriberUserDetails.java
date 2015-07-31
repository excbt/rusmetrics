package ru.excbt.datafuse.nmk.security;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.model.support.SubscriberUser;

public class SubscriberUserDetails extends User implements SubscriberUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6647486217901487396L;

	private final Long id;
	private final Subscriber subscriber;
	private final boolean _system;
	private final int version;

	/**
	 * 
	 * @param sUser
	 * @param password
	 * @param authorities
	 */
	public SubscriberUserDetails(SubscrUser sUser, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(checkNotNull(sUser).getUserName(), password, authorities);
		this.id = sUser.getId();
		this.subscriber = sUser.getSubscriber();
		this.version = sUser.getVersion();
		this._system = false;
	}

	/**
	 * 
	 * @param sUser
	 * @param password
	 * @param authorities
	 */
	public SubscriberUserDetails(SystemUser sUser, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super(checkNotNull(sUser).getUserName(), password, authorities);
		this.id = sUser.getId();
		this.subscriber = sUser.getSubscriber();
		this.version = sUser.getVersion();
		this._system = true;
	}

	@Override
	public Long getId() {
		return id;
	}

	public boolean is_system() {
		return _system;
	}

	@Override
	public Subscriber getSubscriber() {
		return subscriber;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(": ");		
		sb.append("id: ").append(this.id).append("; ");
		sb.append("subscriberId: ").append(this.subscriber).append("; ");
		sb.append("_system: ").append(this._system).append("; ");
		sb.append("version: ").append(this.version).append("; ");
		return sb.toString();
	}

}
