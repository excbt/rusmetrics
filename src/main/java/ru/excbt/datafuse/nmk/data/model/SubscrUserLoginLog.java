package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

@Entity
@Table(name = "subscr_user_login_log")
public class SubscrUserLoginLog extends AbstractPersistableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9177573259994921436L;

	@Column(name = "subscr_user_id")
	private Long subscrUserId;

	@Column(name = "login_date_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date loginDateTime;

	@Column(name = "subscr_user_name")
	private String userName;

	public Long getSubscrUserId() {
		return subscrUserId;
	}

	public void setSubscrUserId(Long subscrUserId) {
		this.subscrUserId = subscrUserId;
	}

	public Date getLoginDateTime() {
		return loginDateTime;
	}

	public void setLoginDateTime(Date loginDateTime) {
		this.loginDateTime = loginDateTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
