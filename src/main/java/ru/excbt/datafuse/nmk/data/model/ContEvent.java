package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.model.support.ContEventTypeModel;

/**
 * События контейнера
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.mm.2015
 *
 */
@Entity
@Table(name = "cont_event")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContEvent extends AbstractPersistableEntity<Long> implements ContEventTypeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5865290731548602858L;

	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;

	@Column(name = "cont_zpoint_id", insertable = false, updatable = false)
	private Long contZPointId;

	@Transient
	private ContEventType contEventType;

	@Column(name = "cont_event_type_id")
	private Long contEventTypeId;

	@Column(name = "cont_service_type")
	private String contServiceType;

	@Column(name = "cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date eventTime;

	@Column(name = "cont_event_registration_time_tz")
	@Temporal(TemporalType.TIMESTAMP)
	private Date registrationTimeTZ;

	@Column(name = "cont_event_message")
	private String message;

	@Column(name = "cont_event_comment")
	private String comment;

	@Version
	private int version;

	@Column(name = "cont_event_deviation")
	private String contEventDeviationKeyname;

	@Override
	public ContEventType getContEventType() {
		return contEventType;
	}

	@Override
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

	public Date getRegistrationTimeTZ() {
		return registrationTimeTZ;
	}

	public void setRegistrationTimeTZ(Date registrationTimeTZ) {
		this.registrationTimeTZ = registrationTimeTZ;
	}

	public Long getContZPointId() {
		return contZPointId;
	}

	public void setContZPointId(Long contZPointId) {
		this.contZPointId = contZPointId;
	}

	public String getContEventDeviationKeyname() {
		return contEventDeviationKeyname;
	}

	@Override
	public Long getContEventTypeId() {
		return contEventTypeId;
	}

	public void setContEventTypeId(Long contEventTypeId) {
		this.contEventTypeId = contEventTypeId;
	}

}
