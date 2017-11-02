package ru.excbt.datafuse.nmk.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.model.support.ContEventTypeModel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Монитор событий
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.06.2015
 *
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_event_monitor_v3")
@JsonInclude(Include.NON_NULL)
public class ContEventMonitorV3 extends AbstractPersistableEntity<Long> implements ContEventTypeModel, ContEventMonitorX {

	/**
	 *
	 */
	private static final long serialVersionUID = 4325254578193002326L;

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@Column(name = "cont_zpoint_id")
	private Long contZPointId;

	@Column(name = "cont_event_id")
	private Long contEventId;

	@Column(name = "cont_event_type_id")
	private Long contEventTypeId;

	@Transient
	private ContEventType contEventType;

	@Column(name = "cont_event_time")
	//@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime contEventTime;

	@Column(name = "cont_event_level")
	private Integer contEventLevel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_event_level_color", insertable = false, updatable = false)
	@JsonIgnore
	private ContEventLevelColorV2 contEventLevelColor;

	@Column(name = "cont_event_level_color")
	private String contEventLevelColorKeyname;

	@Column(name = "last_cont_event_id")
	private Long lastContEventId;

	@Column(name = "last_cont_event_time")
	private LocalDateTime lastContEventTime;

	@Column(name = "worse_cont_event_id")
	private Long worseContEventId;

	@Column(name = "worse_cont_event_time")
	private LocalDateTime worseContEventTime;

	@Column(name = "is_scalar")
	private Boolean isScalar;

    public Long getContObjectId() {
        return this.contObjectId;
    }

    public Long getContZPointId() {
        return this.contZPointId;
    }

    public Long getContEventId() {
        return this.contEventId;
    }

    public Long getContEventTypeId() {
        return this.contEventTypeId;
    }

    public ContEventType getContEventType() {
        return this.contEventType;
    }

    public LocalDateTime getContEventTime() {
        return this.contEventTime;
    }

    public Integer getContEventLevel() {
        return this.contEventLevel;
    }

    public ContEventLevelColorV2 getContEventLevelColor() {
        return this.contEventLevelColor;
    }

    public String getContEventLevelColorKeyname() {
        return this.contEventLevelColorKeyname;
    }

    public Long getLastContEventId() {
        return this.lastContEventId;
    }

    public LocalDateTime getLastContEventTime() {
        return this.lastContEventTime;
    }

    public Long getWorseContEventId() {
        return this.worseContEventId;
    }

    public LocalDateTime getWorseContEventTime() {
        return this.worseContEventTime;
    }

    public Boolean getIsScalar() {
        return this.isScalar;
    }

    public void setContObjectId(Long contObjectId) {
        this.contObjectId = contObjectId;
    }

    public void setContZPointId(Long contZPointId) {
        this.contZPointId = contZPointId;
    }

    public void setContEventId(Long contEventId) {
        this.contEventId = contEventId;
    }

    public void setContEventTypeId(Long contEventTypeId) {
        this.contEventTypeId = contEventTypeId;
    }

    public void setContEventType(ContEventType contEventType) {
        this.contEventType = contEventType;
    }

    public void setContEventTime(LocalDateTime contEventTime) {
        this.contEventTime = contEventTime;
    }

    public void setContEventLevel(Integer ContEventLevel) {
        this.contEventLevel = ContEventLevel;
    }

    public void setContEventLevelColor(ContEventLevelColorV2 contEventLevelColor) {
        this.contEventLevelColor = contEventLevelColor;
    }

    public void setContEventLevelColorKeyname(String contEventLevelColorKeyname) {
        this.contEventLevelColorKeyname = contEventLevelColorKeyname;
    }

    public void setLastContEventId(Long lastContEventId) {
        this.lastContEventId = lastContEventId;
    }

    public void setLastContEventTime(LocalDateTime lastContEventTime) {
        this.lastContEventTime = lastContEventTime;
    }

    public void setWorseContEventId(Long worseContEventId) {
        this.worseContEventId = worseContEventId;
    }

    public void setWorseContEventTime(LocalDateTime worseContEventTime) {
        this.worseContEventTime = worseContEventTime;
    }

    public void setIsScalar(Boolean isScalar) {
        this.isScalar = isScalar;
    }
}
