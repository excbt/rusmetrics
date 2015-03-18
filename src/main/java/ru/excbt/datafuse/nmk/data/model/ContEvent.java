package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.excbt.datafuse.nmk.data.domain.IdEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="cont_event")
public class ContEvent extends IdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
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
	
	
}
