package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="cont_event")
@EntityListeners({AuditingEntityListener.class})
public class ContEvent extends AbstractAuditableEntity<AuditUser, Long> {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5865290731548602858L;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_object_id")
	@JsonIgnore
	private ContObject contObject;
	
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_event_type_id")	
	private ContEventType contEventType;
	
	@Column(name = "cont_service_type")
	private String contServiceType;
	
	@Column(name = "cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date eventTime;

	@Column(name = "cont_event_message")
	private String message;

	@Column(name = "cont_event_comment")
	private String comment;
	
	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;
	
	@Version
	private int version;
	
	public ContObject getContObject() {
		return contObject;
	}

	public void setContObject(ContObject contObject) {
		this.contObject = contObject;
	}

	public ContEventType getContEventType() {
		return contEventType;
	}

	public void setContEventType(ContEventType contEventType) {
		this.contEventType = contEventType;
	}

	public String getContServiceType() {
		return contServiceType;
	}

	public void setContServiceType(String contServiceType) {
		this.contServiceType = contServiceType;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public Long getContObjectId() {
		return contObjectId;
	}

	public void setContObjectId(Long contObjectId) {
		this.contObjectId = contObjectId;
	}	
	
	
}
