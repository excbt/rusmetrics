package ru.excbt.datafuse.nmk.data.model;

import java.time.LocalDateTime;
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

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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
	//@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime contEventTime;

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

}
