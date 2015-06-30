package ru.excbt.datafuse.nmk.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

@Entity
@Table(name = "cont_event_monitor")
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

	@Column(name = "cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date contEventTime;

	@Column(name = "cont_event_level")
	private Integer ContEventLevel;

	@Column(name = "cont_event_level_color")
	private String contEventLevelColor;

	@Column(name = "last_cont_event_id")
	private Long lastContEventId;

	@Column(name = "last_cont_event_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastContEventTime;

}
