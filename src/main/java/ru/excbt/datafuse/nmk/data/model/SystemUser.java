package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.support.SubscriberUser;

/**
 * Системный пользователь
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.03.2015
 *
 */
@Entity
@Table(name = "system_user")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SystemUser extends AbstractAuditableModel implements SubscriberUser {

	/**
	 *
	 */
	private static final long serialVersionUID = 8167917557955085535L;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@JsonIgnore
	@Column(name = "password")
	private String password;

	@Version
	private int version;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "subscriber_id")
	private Subscriber subscriber;

}
