package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.support.ContEventTypeModel;

/**
 * Монитор событий
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.06.2015
 *
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_event_monitor_v2")
@JsonInclude(Include.NON_NULL)
public class ContEventMonitorV2 extends AbstractPersistableEntity<Long> implements ContEventTypeModel {

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

	@Transient
	private ContEventType contEventType;

	@Column(name = "cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date contEventTime;

	@Column(name = "cont_event_level")
	private Integer ContEventLevel;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_event_level_color", insertable = false, updatable = false)
	@JsonIgnore
	private ContEventLevelColorV2 contEventLevelColor;

	@Column(name = "cont_event_level_color")
	private String contEventLevelColorKeyname;

	@Column(name = "last_cont_event_id")
	private Long lastContEventId;

	@Column(name = "last_cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastContEventTime;

	@Column(name = "worse_cont_event_id")
	private Long worseContEventId;

	@Column(name = "worse_cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date worseContEventTime;

	@Column(name = "is_scalar")
	private Boolean isScalar;

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

	@Override
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

	public ContEventLevelColorV2 getContEventLevelColor() {
		return contEventLevelColor;
	}

	public void setContEventLevelColor(ContEventLevelColorV2 contEventLevelColor) {
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

	public String getContEventLevelColorKeyname() {
		return contEventLevelColorKeyname;
	}

	public void setContEventLevelColorKeyname(String contEventLevelColorKeyname) {
		this.contEventLevelColorKeyname = contEventLevelColorKeyname;
	}

	@Override
	public ContEventType getContEventType() {
		return contEventType;
	}

	@Override
	public void setContEventType(ContEventType contEventType) {
		this.contEventType = contEventType;
	}

	public Long getWorseContEventId() {
		return worseContEventId;
	}

	public void setWorseContEventId(Long worseContEventId) {
		this.worseContEventId = worseContEventId;
	}

	public Date getWorseContEventTime() {
		return worseContEventTime;
	}

	public void setWorseContEventTime(Date worseContEventTime) {
		this.worseContEventTime = worseContEventTime;
	}

	public Boolean getIsScalar() {
		return isScalar;
	}

	public void setIsScalar(Boolean isScalar) {
		this.isScalar = isScalar;
	}

}
