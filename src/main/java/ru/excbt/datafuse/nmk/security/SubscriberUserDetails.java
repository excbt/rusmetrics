package ru.excbt.datafuse.nmk.security;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.model.support.SubscriberUser;

/**
 * Информацией об абоненте для авторизации
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.07.2015
 *
 */
public class SubscriberUserDetails extends User implements SubscriberUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6647486217901487396L;

	private final Long id;
	private final Subscriber subscriber;
	private final Boolean isSystem;
	private final int version;
	private final boolean skipServiceFilter;
	private final boolean isBlocked;

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
		this.isSystem = false;
		this.skipServiceFilter = false;
		this.isBlocked = Boolean.TRUE.equals(sUser.getIsBlocked());
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
		this.isSystem = true;
		this.skipServiceFilter = true;
		this.isBlocked = false;
	}

	@Override
	public Long getId() {
		return id;
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
		sb.append("_system: ").append(this.isSystem).append("; ");
		sb.append("version: ").append(this.version).append("; ");
		return sb.toString();
	}

	public Boolean getIsSystem() {
		return isSystem;
	}

	public boolean getSkipServiceFilter() {
		return skipServiceFilter;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

}
