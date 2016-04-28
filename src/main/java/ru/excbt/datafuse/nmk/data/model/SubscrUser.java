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
	@JsonIgnore
	private String password;

	@Version
	private int version;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "subscr_user_role", joinColumns = @JoinColumn(name = "subscr_user_id"),
			inverseJoinColumns = @JoinColumn(name = "subscr_role_id"))
	private List<SubscrRole> subscrRoles = new ArrayList<>();

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "subscriber_id")
	private Subscriber subscriber;

	@Column(name = "subscriber_id", insertable = false, updatable = false)
	private Long subscriberId;

	@JsonIgnore
	@Column(name = "user_uuid", insertable = false, updatable = false)
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<SubscrRole> getSubscrRoles() {
		return subscrRoles;
	}

	public void setSubscrRoles(List<SubscrRole> subscrRoles) {
		this.subscrRoles = subscrRoles;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public UUID getUserUUID() {
		return userUUID;
	}

	public void setUserUUID(UUID userUUID) {
		this.userUUID = userUUID;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	@Override
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	@Override
	public Long getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(Long subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public String getUserEMail() {
		return userEMail;
	}

	public void setUserEMail(String userEMail) {
		this.userEMail = userEMail;
	}

	public Boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Boolean getIsReadonly() {
		return isReadonly;
	}

	public void setIsReadonly(Boolean isReadonly) {
		this.isReadonly = isReadonly;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	public String getUserNickname() {
		return userNickname;
	}

	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}

	public String getDevComment() {
		return devComment;
	}

	public void setDevComment(String devComment) {
		this.devComment = devComment;
	}

}
