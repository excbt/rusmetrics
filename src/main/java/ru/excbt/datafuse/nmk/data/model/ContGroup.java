package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.keyname.ContGroupType;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cont_group")
public class ContGroup extends AbstractAuditableModel {

	/**
		 * 
		 */
	private static final long serialVersionUID = 9133971621136169339L;

	/**
		 * 
		 */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscriber_id")
	@JsonIgnore
	private Subscriber subscriber;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_group_type")
	private ContGroupType contGroupType;

	@Column(name = "cont_group_name")
	private String contGroupName;

	@Column(name = "cont_group_comment")
	private String contGroupComment;

	@Column(name = "cont_group_description")
	private String contGroupDescription;

	@Version
	private int version;

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	public ContGroupType getContGroupType() {
		return contGroupType;
	}

	public void setContGroupType(ContGroupType contGroupType) {
		this.contGroupType = contGroupType;
	}

	public String getContGroupName() {
		return contGroupName;
	}

	public void setContGroupName(String contGroupName) {
		this.contGroupName = contGroupName;
	}

	public String getContGroupComment() {
		return contGroupComment;
	}

	public void setContGroupComment(String contGroupComment) {
		this.contGroupComment = contGroupComment;
	}

	public String getContGroupDescription() {
		return contGroupDescription;
	}

	public void setContGroupDescription(String contGroupDescription) {
		this.contGroupDescription = contGroupDescription;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
