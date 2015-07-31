package ru.excbt.datafuse.nmk.data.model;

import java.util.Collection;
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

import org.hibernate.annotations.Where;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.support.SubscriberUser;


@Entity
@Table(name="subscr_user")
@Where(clause="id > 0 and deleted = 0")
public class SubscrUser extends AbstractAuditableModel implements SubscriberUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7393532811974219624L;

	/**
	 * 
	 */

	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "password")
	private String password;
	
	@Version
	private int version;

	@OneToMany (fetch = FetchType.EAGER)
    @JoinTable(name="subscr_user_role",
    joinColumns=@JoinColumn(name="subscr_user_id"),
    inverseJoinColumns=@JoinColumn(name="subscr_role_id"))
	private Collection<SubscrRole> subscrRoles;	

	@OneToOne (fetch = FetchType.EAGER)
	@JoinColumn(name="subscriber_id")
	private Subscriber subscriber; 
	
	@Column(name = "user_uuid")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID userUUID;
	
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


	public Collection<SubscrRole> getSubscrRoles() {
		return subscrRoles;
	}

	public void setSubscrRoles(Collection<SubscrRole> subscrRoles) {
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

	
}
