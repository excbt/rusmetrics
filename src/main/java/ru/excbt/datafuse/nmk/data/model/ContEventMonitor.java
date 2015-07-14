package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "cont_event_monitor")
@JsonInclude(Include.NON_NULL)
public class ContEventMonitor extends AbstractPersistableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4325254578193002326L;

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@Column(name = "cont_event_id")
	private Long contEventId;

	@Column(name = "cont_event_type_id")
	private Long contEventTypeId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_event_type_id", insertable = false, updatable = false)
	private ContEventType contEventType;

	@Column(name = "cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date contEventTime;

	@Column(name = "cont_event_level")
	private Integer ContEventLevel;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_event_level_color", insertable = false, updatable = false)
	@JsonIgnore
	private ContEventLevelColor contEventLevelColor;

	@Column(name = "cont_event_level_color")
	@Enumerated(EnumType.STRING)
	private ContEventLevelColorKey contEventLevelColorKey;

	@Column(name = "last_cont_event_id")
	private Long lastContEventId;

	@Column(name = "last_cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastContEventTime;

	public Long getContObjectId() {
		return contObjectId;
	}

	public void setContObjectId(Long contObjectId) {
		this.contObjectId = contObjectId;
	}

	public Long getContEventId() {
		return contEventId;
	}

	public void setContEventId(Long contEventId) {
		this.contEventId = contEventId;
	}

	public Long getContEventTypeId() {
		return contEventTypeId;
	}

	public void setContEventTypeId(Long contEventTypeId) {
		this.contEventTypeId = contEventTypeId;
	}

	public Date getContEventTime() {
		return contEventTime;
	}

	public void setContEventTime(Date contEventTime) {
		this.contEventTime = contEventTime;
	}

	public Integer getContEventLevel() {
		return ContEventLevel;
	}

	public void setContEventLevel(Integer contEventLevel) {
		ContEventLevel = contEventLevel;
	}

	public ContEventLevelColor getContEventLevelColor() {
		return contEventLevelColor;
	}

	public void setContEventLevelColor(ContEventLevelColor contEventLevelColor) {
		this.contEventLevelColor = contEventLevelColor;
	}

	public Long getLastContEventId() {
		return lastContEventId;
	}

	public void setLastContEventId(Long lastContEventId) {
		this.lastContEventId = lastContEventId;
	}

	public Date getLastContEventTime() {
		return lastContEventTime;
	}

	public void setLastContEventTime(Date lastContEventTime) {
		this.lastContEventTime = lastContEventTime;
	}

	public ContEventLevelColorKey getContEventLevelColorKey() {
		return contEventLevelColorKey;
	}

	public void setContEventLevelColorKey(
			ContEventLevelColorKey contEventLevelColorKey) {
		this.contEventLevelColorKey = contEventLevelColorKey;
	}

	public ContEventType getContEventType() {
		return contEventType;
	}

	public void setContEventType(ContEventType contEventType) {
		this.contEventType = contEventType;
	}

}
