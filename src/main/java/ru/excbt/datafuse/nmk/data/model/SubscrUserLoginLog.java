package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

/**
 * История входов пользователей в систему
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.10.2015
 *
 */
@Entity
@Table(name = "subscr_user_login_log")
@Getter
@Setter
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

}
