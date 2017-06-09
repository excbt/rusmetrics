package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Пользователи абонента для группы рассылок
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 23.04.2015
 *
 */
@Entity
@Table(name = "subscr_action_user")
@Getter
@Setter
public class SubscrActionUser extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 4055849268733704428L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "user_email")
	private String userEmail;

	@Column(name = "user_phone")
	private String userPhone;

	@Column(name = "user_description")
	private String userDescription;

	@Column(name = "user_comment")
	private String userComment;

	@Version
	private int version;

}
