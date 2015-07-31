package ru.excbt.datafuse.nmk.data.model.security;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.security.Principal;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.SystemUser;

@Deprecated
public class AuditUserPrincipal implements Principal, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1091056312614514056L;

	private final Long id;
	private final String userName;
	private final int version;
	private final boolean _system;
	private final Long subscriberId;

	public AuditUserPrincipal(SubscrUser subscrUser) {
		checkNotNull(subscrUser);
		this.id = subscrUser.getId();
		this.userName = subscrUser.getUserName();
		this.version = subscrUser.getVersion();
		this._system = false;
		this.subscriberId = subscrUser.getSubscriber() == null ? null
				: subscrUser.getSubscriber().getId();
	}

	public AuditUserPrincipal(SystemUser systemUser) {
		checkNotNull(systemUser);
		this.id = systemUser.getId();
		this.userName = systemUser.getUserName();
		this.version = systemUser.getVersion();
		this._system = true;
		this.subscriberId = systemUser.getSubscriber() == null ? null
				: systemUser.getSubscriber().getId();
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public Long getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public int getVersion() {
		return version;
	}

	public boolean is_system() {
		return _system;
	}

	@Override
	public String getName() {
		return this.userName;
	}
}
