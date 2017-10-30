package ru.excbt.datafuse.nmk.data.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.JsonAbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DeletableObject;
import ru.excbt.datafuse.nmk.data.model.support.SubscriberUser;

/**
 * Пользователи абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 26.02.2015
 *
 */
@Entity
@Table(name = "subscr_user")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Getter
@Setter
public class SubscrUser extends JsonAbstractAuditableModel implements SubscriberUser, DeletableObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -7393532811974219624L;

	/**
	 *
	 */

	@Column(name = "user_name", updatable = false)
	private String userName;

	@JsonIgnore
	@Column(name = "first_name", updatable = false)
	private String firstName;

	@JsonIgnore
	@Column(name = "last_name", updatable = false)
	private String lastName;

	@Column(name = "user_nickname")
	private String userNickname;

	@Column(name = "password")
	//@JsonProperty(access = Access.WRITE_ONLY)
	@JsonIgnore
	private String password;

	@Version
	private int version;

	@JsonProperty(access = Access.READ_ONLY)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "subscr_user_role", joinColumns = @JoinColumn(name = "subscr_user_id"),
			inverseJoinColumns = @JoinColumn(name = "subscr_role_id"))
	private List<SubscrRole> subscrRoles = new ArrayList<>();

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "subscriber_id", updatable = false)
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@JsonIgnore
	@Column(name = "user_uuid", insertable = false, updatable = false, columnDefinition = "uuid")
	private UUID userUUID;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "user_comment")
	private String userComment;

	// Uses for Ldap e-mail
	@Column(name = "user_email")
	private String userEMail;

	@Column(name = "is_blocked")
	private Boolean isBlocked;

	@JsonIgnore
	@Column(name = "is_admin")
	private Boolean isAdmin;

	@JsonIgnore
	@Column(name = "is_readonly")
	private Boolean isReadonly;

	// Uses for User contact e-mail
	@Column(name = "contact_email")
	private String contactEmail;

	// Uses for Ldap Description
	@Column(name = "user_description", insertable = true, updatable = false)
	private String userDescription;

	@Column(name = "dev_comment")
	private String devComment;

}
