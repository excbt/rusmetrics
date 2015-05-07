package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "subscr_action_user_group")
public class SubscrActionUserGroup extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3198493937130028852L;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_action_group_id", updatable = false, insertable = false)
	@JsonIgnore
	private SubscrActionGroup subscrActionGroup;

	@Column(name = "subscr_action_group_id")
	private Long subscrActionGroupId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscr_action_user_id", updatable = false, insertable = false)
	@JsonIgnore
	private SubscrActionUser subscrActionUser;

	@Column(name = "subscr_action_user_id")
	private Long subscrActionUserId;

	public SubscrActionGroup getSubscrActionGroup() {
		return subscrActionGroup;
	}

	public void setSubscrActionGroup(SubscrActionGroup subscrActionGroup) {
		this.subscrActionGroup = subscrActionGroup;
	}

	public Long getSubscrActionGroupId() {
		return subscrActionGroupId;
	}

	public void setSubscrActionGroupId(Long subscrActionGroupId) {
		this.subscrActionGroupId = subscrActionGroupId;
	}

	public SubscrActionUser getSubscrActionUser() {
		return subscrActionUser;
	}

	public void setSubscrActionUser(SubscrActionUser subscrActionUser) {
		this.subscrActionUser = subscrActionUser;
	}

	public Long getSubscrActionUserId() {
		return subscrActionUserId;
	}

	public void setSubscrActionUserId(Long subscrActionUserId) {
		this.subscrActionUserId = subscrActionUserId;
	}


}
